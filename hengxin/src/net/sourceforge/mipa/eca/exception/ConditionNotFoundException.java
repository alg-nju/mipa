/* 
 * MIPA - Middleware Infrastructure for Predicate detection in Asynchronous 
 * environments
 * 
 * Copyright (C) 2009 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the term of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.mipa.eca.exception;

import net.sourceforge.mipa.eca.Condition;
import net.sourceforge.mipa.eca.DataSource;

/**
 * Condition Not Found exception used between Condition and Data Source
 *
 * @author Jianping Yu <jianp.yue@gmail.com>
 * @see Condition
 * @see DataSource
 */
public class ConditionNotFoundException extends Exception {

    private static final long serialVersionUID = 621635417163744041L;

    public ConditionNotFoundException() {
        
    }

    /**
     * @param message
     */
    public ConditionNotFoundException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ConditionNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ConditionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
