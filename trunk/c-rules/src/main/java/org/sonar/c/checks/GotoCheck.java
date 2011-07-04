package org.sonar.c.checks;

import org.sonar.check.BelongsToProfile;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonarsource.c.plugin.CCheck;

@Rule(key = "C.DoNotUseGoto", name = "Goto statement must not be used", isoCategory = IsoCategory.Usability, priority = Priority.MAJOR,
	    description = "<p>Avoid using keyword goto.</p>")
@BelongsToProfile(title = CChecksConstants.SONAR_C_WAY_PROFILE_KEY, priority = Priority.MAJOR)
	
public class GotoCheck extends CCheck
{
    @Override
    public void init() {
        subscribeTo(getCGrammar().gotoStatement);
    }
    
    public void visitNode(AstNode node) {
        if (node.is(getCGrammar().gotoStatement)) {
            log("Avoid using keyword goto.", node);
        }
    }
}
