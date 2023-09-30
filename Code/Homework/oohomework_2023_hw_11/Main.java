import com.oocourse.spec3.main.Runner;
import data.MyEmojiMessage;
import data.MyGroup;
import data.MyMessage;
import data.MyNetwork;
import data.MyNoticeMessage;
import data.MyPerson;
import data.MyRedEnvelopeMessage;

public class Main {
    public static void main(String[] args) throws Exception {
        Runner runner = new Runner(MyPerson.class, MyNetwork.class, MyGroup.class, MyMessage.class,
                MyEmojiMessage.class, MyNoticeMessage.class, MyRedEnvelopeMessage.class);
        runner.run();
    }
}
