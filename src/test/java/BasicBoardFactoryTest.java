import com.rieke.bmore.catan.base.board.BasicBoardFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by tcrie on 7/30/2017.
 */
public class BasicBoardFactoryTest {


    private BasicBoardFactory boardFactory;

    @Before
    public void init() {
        boardFactory = new BasicBoardFactory();
    }

    @Test
    public void testGetLayerOfTilesForHosts() {
        for(int i = 1; i<5;i++) {
            List<int[]> hostTiles = boardFactory.getLayerOfTilesForHosts(i);
            System.out.println("Layer "+i+" --------------------");
            for(int[] hosts:hostTiles) {
                System.out.println(""+hosts[0]+","+hosts[1]+","+hosts[2]);
            }
        }
    }
}
