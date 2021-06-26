package lifeGame;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CellTest {
	private static int maxWidth = 20;
	private static int maxLength = 35;
	private static Cell cell=new Cell(maxLength,maxWidth);

	@Before
	public void setUp() throws Exception {
		cell.deleteAllCell();
		cell.setNowGeneration(0);
	}

	@Test
	public void testRandomCell() {
		boolean flag=false;
		cell.randomCell();
		for (int i = 1; i <= maxLength; i++)
            for (int j = 1; j <= maxWidth; j++)
            	if(cell.getGrid()[i][j]!=0)
            		flag=true;
		assertEquals(true,flag);           	
	}

	@Test
	public void testDeleteAllCell() {
		for (int i = 1; i <= maxLength; i++)
            for (int j = 1; j <= maxWidth; j++)
            	assertEquals(0,cell.getGrid()[i][j]);
	}

	@Test
	public void testUpdate() {
		int flag=0;
		int[][] newGrid = new int[maxLength + 2][maxWidth + 2];
		for (int i = 0; i <= maxLength+1; i++)
            for (int j = 0; j <= maxWidth+1; j++)
                newGrid[i][j] = 0;
		cell.setGrid(newGrid);
		assertEquals(0,cell.getNowGeneration());
		cell.update();
		for (int i = 1; i <= maxLength; i++)
            for (int j = 1; j <= maxWidth; j++)
                if(cell.getGrid()[i][j]!=newGrid[i][j])
                	flag=1;
		assertEquals(0,flag);
		
		newGrid[9][8]=0;
		newGrid[9][7]=1;
		newGrid[9][9]=1;
		cell.setGrid(newGrid);
		cell.update();
        newGrid[9][8]=1;
		assertEquals(2,cell.getNowGeneration());
		for (int i = 1; i <= maxLength; i++)
            for (int j = 1; j <= maxWidth; j++)
                if(cell.getGrid()[i][j]!=newGrid[i][j])
                	flag=1;
		assertEquals(0,flag);
		
		for (int i = 0; i <= maxLength+1; i++)
            for (int j = 0; j <= maxWidth+1; j++)
                newGrid[i][j] = 0;
		
		newGrid[10][12]=1;
		newGrid[10][14]=1;
		newGrid[12][12]=1;
		cell.setGrid(newGrid);
		cell.update();
		newGrid[11][13]=1;
		assertEquals(3,cell.getNowGeneration());
		for (int i = 1; i <= maxLength; i++)
            for (int j = 1; j <= maxWidth; j++)
                if(cell.getGrid()[i][j]!=newGrid[i][j])
                	flag=1;
		assertEquals(0,flag);
		
	}

}
