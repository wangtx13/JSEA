package xml.parse;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by wangtianxia1 on 16/8/24.
 */
public class ExtractPhraseLabels {

    private String[] allPhraseLabels;

    public ExtractPhraseLabels(int topicCount) {
        allPhraseLabels = new String[topicCount];

    }

    public String[] getAllPhraseLabels(String phraseLabelFilePath,int topicCount) {
        for(int i = 0; i < topicCount; ++i) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                TopicPhraseHandler handler = new TopicPhraseHandler(i);
                saxParser.parse(phraseLabelFilePath, handler);
//                String phrasesLabels = handler.getMatchedPhrases();
                String phrasesLabels = handler.getTop10MatchedPhrases();
                allPhraseLabels[i] = phrasesLabels;
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return allPhraseLabels;
    }

}
