import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.*;

/**
 * a bolt that finds the top n words.
 */
public class TopNFinderBolt extends BaseBasicBolt {
  private HashMap<String, Integer> currentTopWords = new HashMap<String, Integer>();
  private int N;

  private long intervalToReport = 20;
  private long lastReportTime = System.currentTimeMillis();
    private long leastcount = 0;
    private String leaststring = new String();

  public TopNFinderBolt(int N) {
    this.N = N;
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
 /*
    ----------------------TODO-----------------------
    Task: keep track of the top N words


    ------------------------------------------------- */
	String word = tuple.getString(0);
	Integer count = tuple.getInteger(1);
      if(currentTopWords.isEmpty()){
          leastcount = count;
          leaststring = word;
      }
    if(currentTopWords.containsKey(word) || currentTopWords.size() < this.N){
        currentTopWords.put(word , count);
        if(leastcount > count){
            leastcount = count;
            leaststring = word;
        }
      }
      else if (leastcount < count){
        currentTopWords.put(word , count);
        currentTopWords.remove(leaststring);
        leastcount = count;
        leaststring = word;
        for (Map.Entry<String, Integer> entry : currentTopWords.entrySet()) {
            String curword = entry.getKey();
            Integer curcount = entry.getValue();
            if(leastcount > curcount){
                leastcount = curcount;
                leaststring = curword;
            }
        }
    }
    //reports the top N words periodically
    if (System.currentTimeMillis() - lastReportTime >= intervalToReport) {
      collector.emit(new Values(printMap()));
      lastReportTime = System.currentTimeMillis();
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

     declarer.declare(new Fields("top-N"));

  }

  public String printMap() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("top-words = [ ");
    for (String word : currentTopWords.keySet()) {
      stringBuilder.append("(" + word + " , " + currentTopWords.get(word) + ") , ");
    }
    int lastCommaIndex = stringBuilder.lastIndexOf(",");
    stringBuilder.deleteCharAt(lastCommaIndex + 1);
    stringBuilder.deleteCharAt(lastCommaIndex);
    stringBuilder.append("]");
    return stringBuilder.toString();

  }
}
