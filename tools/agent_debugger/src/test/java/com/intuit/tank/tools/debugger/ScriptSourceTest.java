package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ScriptSourceTest {

    @Test
    public void holdsTheIdAndWhereItCameFrom() {
        ScriptSource source = new ScriptSource("12", SourceType.project);
        assertEquals("12", source.getId());
        assertEquals(SourceType.project, source.getSource());

        source.setId("/tmp/script.xml");
        source.setSource(SourceType.file);
        assertEquals("/tmp/script.xml", source.getId());
        assertEquals(SourceType.file, source.getSource());
    }

    @Test
    public void sourceTypes() {
        assertArrayEquals(new SourceType[] { SourceType.project, SourceType.script, SourceType.file },
                SourceType.values());
        assertEquals(SourceType.script, SourceType.valueOf("script"));
    }
}
