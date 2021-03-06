package net.sourceforge.pmd.eclipse.ui.preferences.br;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.ui.IndexedString;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class RuleUIUtilTest {

    /**
     * There should be no UnsupportedOperationException...
     */
    @Test
    public void testTndexedPropertyStringFromRule() {
        Rule rule = new XPathRule();
        IndexedString s = RuleUIUtil.indexedPropertyStringFrom(rule);
        assertNotNull(s);
    }
}
