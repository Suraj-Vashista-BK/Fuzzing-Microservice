import static org.junit.Assert.*;
import org.example.*;
import org.junit.Test;

public class demo {

    @Test
    public void CalculatorTest(){
        Integer a = 50;
        Integer b = 50;
        assertEquals(Integer.valueOf(100), Main.calculator(a, b));

    }
}
