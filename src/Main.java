import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    public enum Lexeme
    {
        Whitespace("[\\s]+"),
        Number("\\b([0-9]\\d*(\\.\\d+)?|0[xX][0-9a-fA-F]+)\\b"),
        String("\"[^\"]*\"|'[^']*'|`[^`]*`"),
        Comment("//.*"),
        Keyword("\\b(abstract|as|base|bool|break|byte|case|catch|char|checked|class|const|continue|decimal|default|delegate|do|double|else|enum|event|explicit|extern|false|finally|fixed|float|for|foreach|goto|if|implicit|in|int|interface|internal|is|lock|long|namespace|new|null|object|operator|out|override|params|private|protected|public|readonly|ref|return|sbyte|sealed|short|sizeof|stackalloc|static|string|struct|switch|this|throw|true|try|typeof|uint|ulong|unchecked|unsafe|ushort|using|virtual|void|volatile|while)\\b"),
        Identifier("\\b[a-zA-Z_$][a-zA-Z_$0-9]*\\b"),
        Operator("(\\+\\+|--|\\+|-|~|!|%|\\/|\\*|<<|>>|>>>|<|>|<=|>=|==|!=|&|\\^|\\||&&|\\|\\||=|\\+=|-=|\\*=|/=|%=|&=|\\^=|\\|=|<<=|>>=|>>>=|\\?|\\:)"),
        Punctuation("[;:,\\.\\{\\}\\[\\]\\(\\)]");

        final String pattern;

        Lexeme(String pattern)
        {
            this.pattern = pattern;
        }
    };

    public static void main(String[] args)
    {
        parseCode();
    }

    public static void parseCode()
    {
        String text;
        try
        {
            text = Files.readString(Path.of("sharpcode.txt"));
        }
        catch (IOException e)
        {
            System.err.println("Error reading file -> " + e);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Lexeme type : Lexeme.values())
        {
            stringBuilder.append(String.format("|(%s)", type.pattern));
        }

        Pattern pattern = Pattern.compile(stringBuilder.substring(1));
        Matcher match = pattern.matcher(text);

        int place = 0;
        while (match.find())
        {
            if (place != match.start())
            {
                System.err.println("\nInvalid token -> " + text.substring(place, match.start()));
                return;
            }
            else
            {
                place = match.end();
            }
            for (Lexeme type: Lexeme.values())
            {
                if (match.group().matches(type.pattern))
                {
                    if (type != Lexeme.Whitespace && type != Lexeme.Comment)
                    {
                        System.out.println(match.group() + " - " + type.name());
                    }
                    break;
                }
            }
        }
    }
}