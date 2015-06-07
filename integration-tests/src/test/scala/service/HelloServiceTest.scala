package service

import org.junit.Test;
import org.junit.Assert.assertEquals

class HelloServiceTest
{
    @Test
    def test1()
    {
        assertEquals("Hello", HelloService.hello)
    }

}
