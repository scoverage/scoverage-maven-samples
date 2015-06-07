package service

import org.junit.Test;
import org.junit.Assert.assertEquals

class HelloServiceIT
{
    @Test
    def test1()
    {
        assertEquals("Hello2", HelloService.hello2)
    }

}
