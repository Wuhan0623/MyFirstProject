import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lib
{
    int sumLines = 0;
    int sumChars = 0;
    int sumWords = 0;
    String resultStr = "";    /* a string formed by splicing articles */
    Map<String,Integer> map = new HashMap<String, Integer>();    /* word frequency statistics */
    String sortResultStr = "";    /*a string formed by 10 sorted words*/

    /*get BufferReader by file path*/
    public static BufferedReader getBufferedReader(String path)
    {
        FileReader fr = null;
        try
        {
            fr = new FileReader(path);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("err, file is not found!");
            e.printStackTrace();
        }
        assert fr != null;
        return new BufferedReader(fr);
    }

    /*get a string formed by the file*/
    public String readToString(String path)
    {
        String encoding = "UTF-8";
        File file = new File(path);
        long filelength = file.length();
        byte[] filecontent = new byte[(int) filelength];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            return new String(filecontent, encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            System.err.println(" OS is not supported! " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /*count the number of characters*/
    public void charCount(String path)
    {
        this.resultStr = readToString(path);
        //this.resultStr = this.resultStr.replace('\r', ' ');
        //this.resultStr = this.resultStr.replace('\n', ' ');
        this.sumChars = this.resultStr.length();
    }

    /*delete some character*/
    public String stringTrimAll(String input)
    {
        if (null == input)
            return "";
        final String regx = "\\s*|\t|\r|\n";
        Pattern patt = Pattern.compile(regx);
        Matcher m = patt.matcher(input);
        return m.replaceAll("");
    }

    /*count the number of the lines which is not blank*/
    public void lineCount(String path)
    {
        String temp;
        this.sumLines = 0;
        try
        {
            BufferedReader br = getBufferedReader(path);
            temp = br.readLine();
            while(temp != null)
            {
                temp = stringTrimAll(temp);
                if(temp.length() > 0)
                {
                    this.sumLines ++;
                }
                temp = br.readLine();
            }
        }
        catch (IOException e)
        {
            System.err.println("the err of line count!");
            e.printStackTrace();
        }
    }

    /*count the number of the right words*/
    public void wordCount(String path)
    {
        this.resultStr = readToString(path);
        //this.resultStr = this.resultStr.replace('\r', ' ');
        //this.resultStr = this.resultStr.replace('\n', ' ');
        this.resultStr = this.resultStr.replaceAll("[^A-Za-z0-9]", " ");
        this.resultStr = this.resultStr.toLowerCase();
        String[] words = this.resultStr.split(" ");    // divide with spaces

        this.sumWords = 0;
        for (String word : words)
        {
            if (word.length() > 3)
            {
                int j;
                for (j = 0; j < 4; j++)
                {
                    char x = word.charAt(j);
                    if (x < 'a' || x > 'z') break;
                }
                if (j == 4) this.sumWords++;
            }
        }
    }

    /*Words' class*/
    public class Word implements Comparable<Word>
    {
        String key;
        Integer value;

        public Word(String key, Integer value)
        {
            this.key = key;
            this.value = value;
        }
        @Override
        public int compareTo(Word word)
        {
            int x = word.value.intValue() - this.value.intValue();
            return (x == 0 ? this.key.compareTo(word.key) : x);
        }
    }

    /*count the frequency of words*/
    public void wordFrequencyCount(String path)
    {
        this.resultStr = readToString(path);
        //this.resultStr = this.resultStr.replace('\r', ' ');
        //this.resultStr = this.resultStr.replace('\n', ' ');
        this.resultStr = this.resultStr.replaceAll("[^A-Za-z0-9]", " ");
        this.resultStr = this.resultStr.toLowerCase();
        String[] words = this.resultStr.split(" ");    // divide with spaces

        for(String word : words)
        {
            if (word.length() > 3)
            {
                int j;
                for (j = 0; j < 4; j++)
                {
                    char x = word.charAt(j);
                    if (x < 'a' || x > 'z') break;
                }
                if(j == 4)
                {
                    int x = 0;
                    if(this.map.get(word) == null)  x++;
                    else  x = this.map.get(word).intValue() + 1;
                    this.map.put(word, x);
                }
            }
        }

        Set<Word> set = new TreeSet<Word>();
        for(String key : this.map.keySet())
        {
            set.add(new Word(key, this.map.get(key)));
        }
        int sum = 0;
        Iterator<Word> t = set.iterator();
        while (t.hasNext())
        {
            Word word = t.next();
            this.sortResultStr += word.key + ": " + word.value + "\n";
            sum++;
            if(sum == 10)  break;
        }
    }

    /*output the result text to a file*/
    public static void outputFile(String result, String outputPath)
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath), "utf-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        writer.print(result);
        writer.close();
    }

    /*test*/
    public static String getRandomString()
    {
        int[] num = new int[96];
        char ch;
        StringBuffer sb = new StringBuffer();
        /*空白符*/
        num[0] = 9;
        num[1] = 10;
        num[2] = 13;
        num[3] = 32;
        /*数字*/
        for(int i = 0; i < 10 ; i++)
        {
            num[i+4] = i + '0';
        }
        /*小写字母*/
        for(int i = 0; i < 26 ; i++)
        {
            num[i+14] = i + 'a';
        }
        /*大写字母*/
        for(int i = 0; i < 26 ; i++)
        {
            num[i+40] = i + 'A';
        }
        /*额外增加空格的数量,控制单词长度*/
        for(int i = 0; i < 30 ; i++)
        {
            num[i+66] = ' ';
        }

        int x;
        for (int i = 0; i < 1000000; i++)
        {
            x = ThreadLocalRandom.current().nextInt(96);
            sb.append((char) num[x]);
        }
        return sb.toString();
    }

    /*output to file*/
    public static void writeToFile(String content, String inPath)
    {
        FileWriter fw = null;
        try {
            //创建字符输出流对象，负责向文件内写入
            fw = new FileWriter(inPath);
            //将str里面的内容读取到fw所指定的文件中
            fw.write(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(fw!=null){
                try {
                    fw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


}
