package hexlet.code.app.util;


public final class StatusSlugUtils {
    public static String statusNameFromSlug(String slug) {
        if ((slug.contains("_"))) {
            String[] words = slug.split("_");
            for (int i = 0; i < words.length; i++) {
                words[i] = Character.toUpperCase(words[i].charAt(0))
                        + words[i].substring(1);
            }
            String processedSlug = String.join("", words);
            return processedSlug;
        } else {
            String result = "";
            Character firstChar = Character.toUpperCase(slug.charAt(0));
            String lastPart = slug.substring(1);
            return result + firstChar + lastPart;
        }
    }
}
