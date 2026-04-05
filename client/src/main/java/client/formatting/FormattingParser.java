// Parser ühest minu varasemast projektist -- MV
// Ehk siis hindamise mõttes, see on ikka spetsiifiliselt selle rühmatöö jaoks
// loodud (st mitte lihtsalt copy pastitud kuskilt), aga ma tegin selle valmis
// iseseisva projektina ja siis tõin ta siia projekti.

package client.formatting;

import java.util.Iterator;

/**
 * Parses a subset of Markdown into "text sections" where each section has a
 * defined style. Currently only supports bold and italics.
 */
public class FormattingParser implements Iterator<TextSection> {
    private static final String BOLD_DELIMITER = "**";
    private static final String ITALIC_DELIMITER = "*";
    private static final String MENTION_PREFIX = "@";

    private final CharSequence src;
    private final Style currentStyle = new Style();

    private int cursor = 0;

    public FormattingParser(CharSequence src) {
        this.src = src;
    }

    private static boolean allowedInUsername(char ch) {
        return ch >= 'A' && ch <= 'Z' ||
               ch >= 'a' && ch <= 'z' ||
               ch >= '0' && ch <= '9' ||
               ch == '_';
    }

    @Override
    public boolean hasNext() {
        return cursor < src.length();
    }

    @Override
    public TextSection next() {
        if (currentlyStartingWith(MENTION_PREFIX)) {
            TextSection mention = takeMention();
            if (mention != null) {
                return mention;
            }
        }

        checkForBold();
        checkForItalics();

        return takePlain();
    }

    private TextSection takeMention() {
        assert currentlyStartingWith(MENTION_PREFIX);

        int start = cursor;
        while (cursor < src.length()) {
            if (cursor > start && !allowedInUsername(src.charAt(cursor))) {
                break;
            }
            cursor++;
        }

        CharSequence subSeq = src.subSequence(start, cursor);
        if (subSeq.isEmpty()) {
            return null;
        }

        Style mentionStyle = new Style();
        mentionStyle.setMention(true);
        return new TextSection(subSeq, mentionStyle);
    }

    private boolean checkFor(CharSequence delimiter) {
        if (currentlyStartingWith(delimiter)) {
            skip(delimiter.length());
            return true;
        }
        return false;
    }

    private void checkForBold() {
        if (checkFor(BOLD_DELIMITER)) {
            currentStyle.setBold(!currentStyle.isBold());
        }
    }

    private void checkForItalics() {
        if (checkFor(ITALIC_DELIMITER)) {
            currentStyle.setItalic(!currentStyle.isItalic());
        }
    }

    private void skip(int n) {
        cursor += n;
    }

    /**
     * Assuming the character at the cursor is not a special delimiter, this
     * method takes the next section of text until a delimiter is encountered.
     *
     * @return An unstyled section
     */
    private TextSection takePlain() {
        int start = cursor;
        while (cursor < src.length() && !isSpecialDelimiter()) {
            cursor++;
        }

        CharSequence subSeq = src.subSequence(start, cursor);
        return new TextSection(subSeq, currentStyle);
    }

    private boolean isSpecialDelimiter() {
        return currentlyStartingWith(BOLD_DELIMITER) ||
                currentlyStartingWith(ITALIC_DELIMITER) ||
                currentlyStartingWith(MENTION_PREFIX);
    }

    private boolean currentlyStartingWith(CharSequence prefix) {
        if (src.length() - cursor < prefix.length()) {
            return false;
        }

        for (int i = 0; i < prefix.length(); i++) {
            if (src.charAt(cursor + i) != prefix.charAt(i)) {
                return false;
            }
        }

        return true;
    }
}
