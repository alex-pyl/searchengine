package searchengine.utils;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.config.ConnectConfig;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    private static LuceneMorphology luceneMorph;
    private static final String RUSSIAN = "[^А-Яа-яёЁ]+";
    private static final String TAG_SCRIPT = "<script\\b[^<]*(?:(?!<\\/script>)<[^<]*)*<\\/script\\s*>";
    private static final String TAG_STYLE = "<style\\b[^<]*(?:(?!<\\/style>)<[^<]*)*<\\/style\\s*>";
    private static final String TAG_ANY = "<[^>]*>";
    private static final String WHITESPACE = "\\s+";
    private static final List<String> SERVICE_PARTS = List.of("МЕЖД", "ПРЕДЛ", "СОЮЗ", "ЧАСТ");
    private static final int SNIPPET_LENGTH = 140;

    public static void setLuceneMorph() throws IOException {
        if (luceneMorph == null) {
            luceneMorph = new RussianLuceneMorphology();
        }
    }

    public static Map<String, Long> doLucene(String text) throws IOException {
        List<String> result = new ArrayList<>();
        String[] words = text.toLowerCase().split(RUSSIAN);
        setLuceneMorph();
        for (String word : words) {
            if (!word.isEmpty()) {
                List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
                if (wordBaseForms.stream().flatMap(w -> Arrays.stream(w.split(" "))).noneMatch(SERVICE_PARTS::contains)) {
                    result.addAll(luceneMorph.getNormalForms(word));
                }
            }
        }
        return result.stream().collect(Collectors.groupingBy(String::new, Collectors.counting()));
    }

    public static String removeTags(String text) {
        return text.replaceAll(TAG_SCRIPT, "").replaceAll(TAG_STYLE, "").replaceAll(TAG_ANY, "").replaceAll(WHITESPACE, " ").trim();
    }

    public static String getTitle(String text) {
        Document doc = Jsoup.parse(text);
        return doc.title();
    }

    public static String getSnippet(String text, List<String> words) {
        String result = removeTags(text).toLowerCase();
        boolean firstWord = true;
        for (String word : words) {
            int position = -1;
            int length = word.length();
            for (; length > 3; length--) {
                word = word.substring(0, length);
                position = (" " + result).indexOf(" " + word);
                if (position >= 0) {
                    break;
                }
            }
            if (position >= 0) {
                if (firstWord) {
                    result = result.substring(Math.max(position - SNIPPET_LENGTH, 0), Math.min(position + length + SNIPPET_LENGTH, result.length()));
                    var resultWords = result.split(WHITESPACE);
                    result = String.join(" ", Arrays.copyOfRange(resultWords, 1, resultWords.length - 1));
                    firstWord = false;
                }
                result = result.replace(word, "<b>" + word + "</b>");
            }
        }
        return result;
    }

    public static WebResponse connect(ConnectConfig config, String url) {
        WebResponse result = new WebResponse();
        try {
            Connection.Response response = Jsoup.connect(url).userAgent(config.getUseragent()).referrer(config.getReferrer()).execute();
            result.setStatus(response.statusCode());
            result.setDocument(response.parse());
        } catch (IOException e) {
            result.setError(e.getMessage());
        }
        return result;
    }

}
