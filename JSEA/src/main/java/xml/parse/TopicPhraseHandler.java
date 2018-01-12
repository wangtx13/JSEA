package xml.parse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by wangtianxia1 on 16/8/23.
 */
public class TopicPhraseHandler extends DefaultHandler {
    private boolean phraseName;
    private boolean matched;
    private int id;
    private int matchedID;
    private String matchedPhrases;

    public TopicPhraseHandler(int matchedID) {
        phraseName = false;
        matched = false;
        id = -1;
        this.matchedID = matchedID;
        matchedPhrases = "";
    }


    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("TOPIC")) {
            id = Integer.parseInt(attributes.getValue("id"));
            if(id == matchedID) {
                matched = true;
            }
        }

        if (qName.equalsIgnoreCase("PHRASE")) {
            phraseName = true;
        }
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        //id匹配且一个topic的所有phrase已经取得
        if(matched && qName.equalsIgnoreCase("TOPIC")) {
            matched = false;
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {

        if(phraseName) {
            phraseName = false;
            if(matched) {
                matchedPhrases = matchedPhrases + new String(ch, start, length) + "; ";
            }

        }
    }

//    public String getMatchedPhrases() {
//            return matchedPhrases;
//    }

    public String getTop10MatchedPhrases() {
        if (matchedPhrases == null) return matchedPhrases;
        String[] phrases = matchedPhrases.split("; ");
        String newPhrases = "";
        if (phrases.length > 0) {
            for(int i = 0; i < phrases.length && i < 5; i++) {
                if(phrases[i].length() < 25)
                    newPhrases = newPhrases + phrases[i] + "; ";
            }
            return newPhrases;
        } else {
            return matchedPhrases;
        }
    }
}

