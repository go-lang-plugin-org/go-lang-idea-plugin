package ro.redeul.google.go.lang.scoping;

import java.util.Set;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:30:13 PM
 */
public class GoScopeManager {

    class Scope {
        Scope parentScope;

        Set<String> names;

        Scope(Scope parentScope) {
            this.parentScope = parentScope;
        }

        Scope createScope() {
            return new Scope(this);
        }

        Scope getParentScope() {
            return parentScope;
        }

        void defineName(String name) {
            names.add(name);
        }

        boolean isDefined(String name) {
            return names.contains(name) || (parentScope != null && parentScope.isDefined(name));
        }
    }
}
