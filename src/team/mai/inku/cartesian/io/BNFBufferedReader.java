package team.mai.inku.cartesian.io;

import team.mai.inku.cartesian.KtCartesianItemProcessor;
import team.mai.inku.cartesian.model.Item;
import team.mai.inku.cartesian.model.OptionItem;
import team.mai.inku.cartesian.model.SequenceItem;
import team.mai.inku.cartesian.model.SimpleItem;
import team.mai.inku.cartesian.util.Comparing;

import java.io.*;
import java.util.*;

public class BNFBufferedReader implements BNFReader {

    private BufferedReader reader;

    public BNFBufferedReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    }

    public BNFBufferedReader(String path) throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    }

    public BNFBufferedReader(FileInputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public BNFBufferedReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public static void main(String[] args) {
        KtCartesianItemProcessor processor = new KtCartesianItemProcessor();
        String test = "一二三";
        BNFBufferedReader reader = new BNFBufferedReader(new BufferedReader(new StringReader(test)));
        try {
            OptionItem optionItem = reader.read();
            OptionItem extracted = processor.extractToOptionItem(optionItem);
            for (Item item : extracted.getOptions()) {
                System.out.println(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public OptionItem read() throws IOException {
        if (reader == null)
            throw new IllegalStateException("setReader or use constructor with arguments!");
        OptionItem result = new OptionItem();
        int i = 0;
        while (true) {
            i++;
            String s = reader.readLine();
            if (s == null)
                break;
            int error = checkBrackets(s);
            if (error < 0) {
                Item item = resolveString(s, 0, s.length());
                result.getOptions().add(item);
            } else {
                System.err.println("Bracket mismatch at line " + i);
                System.err.println(s);
                for (int j = 0; j < error; j++) {
                    System.err.print(" ");
                }
                System.err.println("^");
                throw new IllegalArgumentException("\"Bracket mismatch at line \"+i");
            }
        }
        return result;
    }

    /**
     * @param s     string
     * @param start bracket inclusive
     * @param end   bracket inclusive +1
     * @return
     */
    private Item resolveString(String s, int start, int end) {
        //<.....>
        //^st    ^end
        if (start >= end)
            return new SimpleItem("");
        int firstIndex = Comparing.firstIndex(s, start, '<', '(', '[', '|');
        if (firstIndex < end && firstIndex >= start) {
            // bracket appears inside the object
            switch (s.charAt(firstIndex)) {
                case '(':
                case '<':
                case '[':
                    // insert simple text so far
                    int closurePosition = findBracketClosurePosition(s, firstIndex, end);
                    if (closurePosition + 1 < end) {
                        // ...<................................>|
                        //    ^firstIdx         closurePosition^^nextMark
                        char nextMark = s.charAt(closurePosition + 1);
                        switch (nextMark) {
                            case '[':
                            case '(':
                            case '<':
                                //<...><...>
                                return resolveSequenceItem(s, start, end);
                            case '|':
                                //<...>|<...>, ...|...  or ...<...>| <--syntax error
                                return resolveOptionItem(s, start, end);
                            default:
                                //<...>...
                                return resolveSequenceItem(s, start, end);
                        }
                    } else if (firstIndex == start) {
                        // <................................>
                        // ^firstIdx         closurePosition^^END
                        // wrapped, if[] then goto option resolve, otherwise trim and resolve
                        if (s.charAt(firstIndex) == '[') {
                            return resolveOptionItem(s, start, end);
                        } else {
                            return resolveString(s, start + 1, end - 1);
                        }
                    } else if (closurePosition + 1 == end) {
                        // ...<................................>
                        //    ^firstIdx         closurePosition^^END
                        return resolveSequenceItem(s, start, end);
                    } else {
                        //
                        throw new IllegalStateException();
                    }
                case '|':
                    // ...|...
                    return resolveOptionItem(s, start, end);
                default:
                    throw new IllegalStateException();
            }// switch-end
        } else {
            // if no mark found in range
            return new SimpleItem(s.substring(start, end));
        }
    }

    /**
     * @param s
     * @param start including the bracket(if contains)
     * @param end   including the bracket(if contains) +1
     * @return
     */
    private OptionItem resolveOptionItem(String s, int start, int end) {
        // if wrapped by [] then optional
        boolean isOptional;
        // analyze and remove brackets
        switch (s.charAt(start)) {
            case '(':
            case '<':
                isOptional = false;
                if (findBracketClosurePosition(s, start, end) == end - 1) {
                    // <...|...|...> wrapped
                    // ^start       ^end
                    start++;
                    end--;
                    System.err.println("resolveOptionItem is unwrapping <>");
                    System.err.println("This should not happen. <> and () wrapped object should be trimmed by resolveString()!");
                    System.err.println("\t" + s.substring(start, end));
                }
                break;
            case '[':
                isOptional = true;
                if (findBracketClosurePosition(s, start, end) == end - 1) {
                    // [...|...|...] wrapped
                    // ^start       ^end
                    start++;
                    end--;
                }
                break;
            default:
                // plain ...|...|...
                isOptional = false;
                break;
        }
        // to store final result
        List<Item> options = new ArrayList<>();
        // if optional, add empty case
        if (isOptional)
            options.add(new SimpleItem(""));
        // actual analyzing
        int pos = start;
        while (pos < end) {
            // get start char
            char mark = s.charAt(pos);
            switch (mark) {
                case '[':
                case '<':
                case '(':
                    // look for the end of the bracket
                    // [....]
                    // ^pos ^closurePos
                    int closurePosition = findBracketClosurePosition(s, pos, end);
                    // resolve anything in the bracket
                    Item item = resolveString(s, pos, closurePosition + 1);
                    pos = closurePosition + 1;
                    // [....]|<...>
                    //       ^pos
                    // expect '|' after any expression closure
                    if (pos>=end||s.charAt(pos) == '|') {
                        options.add(item);
                        // [....]|<...>
                        //        ^pos
                        pos++;
                    } else {
                        throw new IllegalArgumentException("\"" + s.substring(start, end) + "\" is OptionItem " +
                                "but found none '|' char after bracket '" + s.charAt(closurePosition) + "' at position " + (pos-start));
                    }
                    break;
                case '|':
                    // empty case
                    options.add(new SimpleItem(""));
                    pos++;
                    break;
                default:
                    // ...|...
                    // literals, find next '|'
                    int nextSplit = s.indexOf('|', pos);
                    int synErrPos = Comparing.firstIndex(s, pos, '<', '(', '[');
                    // if syntax error
                    // like ...<.........>|
                    //         ^synErrPos ^nextSplit
                    // or   ...<.........> END
                    if (
                            (nextSplit > 0 && synErrPos > 0) &&
                            (nextSplit < end && synErrPos < nextSplit)
                    ) {
                        System.err.println(s.substring(pos, end));
                        for (int j = 0; j < synErrPos - 1; j++)
                            System.err.print(" ");
                        System.err.println("^");
                        throw new IllegalArgumentException("\"" + s.substring(start, end) + "\" is OptionItem " +
                                "but found literal content and then bracket placed between '|'s at " + (synErrPos - pos));
                    }
                    if (nextSplit < 0 || nextSplit >= end) {
                        //There is no more case, read until end and add option
                        options.add(new SimpleItem(s.substring(pos, end)));
                        pos = end;
                        break;
                    }
                    // pos ~ closure(exclusive) is a literal item
                    // ABC|DEF
                    //    ^nextSplit
                    options.add(new SimpleItem(s.substring(pos, nextSplit)));
                    pos = nextSplit + 1;
                    break;
            }
        }
        return new OptionItem(options);
    }

    private SequenceItem resolveSequenceItem(String s, int start, int end) {
        switch (s.charAt(start)) {
            case '(':
            case '<':
            case '[':
                if (findBracketClosurePosition(s, start, end) == end - 1) {
                    // <...|...|...> wrapped
                    // ^start       ^end
                    start++;
                    end--;
                    System.err.println("resolveSequenceItem is unwrapping <>");
                    System.err.println("This should not happen. <> and () wrapped object should be trimmed by resolveString()!");
                    System.err.println("\t" + s.substring(start, end));
                }
                break;
//            case '[':
//                throw new IllegalStateException("Object '" + s.substring(start, end) + "' is being processed by resolveSequenceItem!");
            default:
                // start with plain ...<>
                break;
        }
        List<Item> sequence = new ArrayList<>();
        int pos = start;
        while (pos < end) {
            switch (s.charAt(pos)) {
                case '<':
                case '(':
                case '[':
                    // ...<........>....
                    //    ^pos     ^closure
                    int closure = findBracketClosurePosition(s, pos, end);
                    Item item = resolveString(s, pos, closure + 1);
                    sequence.add(item);
                    pos = closure + 1;
                    break;
                case '|':
                    throw new IllegalArgumentException("Object '" + s.substring(start, end) + "' is considered a sequence, but appeared '|' inside at " + (pos - start));
                default:
                    // literal
                    // .......<...>...
                    // ^pos   ^nextMark
                    int nextMark = Comparing.firstIndex(s, pos, '<', '(', '[', '|');
                    if (nextMark < 0 || nextMark >= end) {
                        // no more mark until end, add and set pos
                        sequence.add(new SimpleItem(s.substring(pos, end)));
                        pos = end;
                        break;
                    }
                    if (s.charAt(nextMark) == '|') {
                        throw new IllegalArgumentException("Object '" + s.substring(start, end) + "' is considered a sequence, but appeared '|' inside at " + (nextMark - start));
                    }
                    sequence.add(new SimpleItem(s.substring(pos, nextMark)));
                    pos = nextMark;
            }
        }
        return new SequenceItem(sequence);
    }


    /**
     * @param s          string
     * @param firstIndex index of bracket to find
     * @param end        position to find until, exclusive(+1)
     * @return position charAt the bracket closes
     * @throws IllegalArgumentException if bracket does not match
     */
    private int findBracketClosurePosition(String s, int firstIndex, int end) {
        char openChar = s.charAt(firstIndex);
        char closureChar;
        switch (openChar) {
            case '[':
                closureChar = ']';
                break;
            case '(':
                closureChar = ')';
                break;
            case '<':
                closureChar = '>';
                break;
            case '{':
                closureChar = '}';
                break;
            default:
                throw new IllegalArgumentException("Character " + openChar + " is not a valid bracket open char");
        }
        int stack = 1;
        int i = firstIndex + 1;
        for (; i < end; i++) {
            if (s.charAt(i) == openChar) {
                stack++;
            } else if (s.charAt(i) == closureChar) {
                stack--;
            }
            if (stack == 0)
                break;
        }
        if (stack == 0)
            return i;
        else
            throw new IllegalArgumentException("Brackets does not match for " + s.substring(firstIndex, end));
    }

    /**
     * @param s string to check
     * @return -1 if matches, otherwise the mismatch position
     */
    private int checkBrackets(String s) {
        Stack<Character> stack = new Stack<>();
        char pop;
        int i = 0;
        try {
            for (; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '<':
                    case '(':
                    case '[':
                    case '{':
                        stack.push(c);
                        break;
                    case '>':
                        pop = stack.pop();
                        if (pop != '<')
                            return i;
                        break;
                    case ')':
                        pop = stack.pop();
                        if (pop != '(')
                            return i;
                        break;
                    case ']':
                        pop = stack.pop();
                        if (pop != '[')
                            return i;
                        break;
                    case '}':
                        pop = stack.pop();
                        if (pop != '{')
                            return i;
                        break;
                }
            }
        } catch (EmptyStackException e) {
            return i;
        }
        if (stack.isEmpty()) {
            return -1;
        } else {
            return i;
        }
    }


}
