package io.github.dug22.jjournal.utils;

import java.util.List;

public class MarkdownToHtml {


    private abstract static class FormatElement {

        public abstract String format(String text);
    }

    private static class Escape extends FormatElement {

        @Override
        public String format(String text) {
            return text == null ? "" : text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }


    private static class Header extends FormatElement {

        @Override
        public String format(String text) {
            return text.replaceAll("(?m)^# (.*)$", "<h1>$1</h1>")
                    .replaceAll("(?m)^## (.*)$", "<h2>$1</h2>")
                    .replaceAll("(?m)^### (.*)$", "<h3>$1</h3>")
                    .replaceAll("(?m)^#### (.*)$", "<h4>$1</h4>")
                    .replaceAll("(?m)^##### (.*)$", "<h5>$1</h5>")
                    .replaceAll("(?m)^###### (.*)$", "<h6>$1</h6>");
        }
    }

    private static class Inline extends FormatElement {

        @Override
        public String format(String text) {
            // bold
            text = text.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
            text = text.replaceAll("__(.+?)__", "<b>$1</b>");

            // italic
            text = text.replaceAll("\\*(.+?)\\*", "<i>$1</i>");
            text = text.replaceAll("_(.+?)_", "<i>$1</i>");

            // inline code
            text = text.replaceAll("`(.+?)`", "<tt>$1</tt>");

            // links
            text = text.replaceAll("\\[(.+?)\\]\\((.+?)\\)", "<a href=\"$2\">$1</a>");
            return text;
        }
    }

    private static class LineBreak extends FormatElement {
        @Override
        public String format(String text) {
            return text.replaceAll("(\r\n|\n|\r)", "<br>");
        }
    }

    private static class HorizontalRule extends FormatElement {

        @Override
        public String format(String text) {
            return text.replaceAll("(?m)^-{3,}\\s*$", "<hr>");
        }
    }

    private static class Lists extends FormatElement {

        @Override
        public String format(String text) {
            return text.replaceAll("(?m)^[-*+] (.*)$", "<p>• $1</p>");
        }
    }

    private static final List<FormatElement> formatElements = List.of(new Escape(), new Header(), new HorizontalRule(), new Lists(), new Inline(), new LineBreak());

    public static String render(String text) {
        for(FormatElement formatElement : formatElements){
            text = formatElement.format(text);
        }
        return "<html>" +
               "<body style='color: white; font-family: sans-serif;'>" +
               text +
               "</body></html>";
    }
}
