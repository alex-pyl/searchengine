package searchengine.utils;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

@Getter
@Setter
public class WebResponse {
    private Document document;
    private int status;
    private String error;
}
