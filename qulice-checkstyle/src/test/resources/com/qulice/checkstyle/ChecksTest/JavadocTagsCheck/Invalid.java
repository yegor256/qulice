/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

/**
 * This comment doesn't have 'author' and 'version' tags.
 */
public final class Invalid {
    public void main() {
    }
}

/**
 * @author John Smith - incorrect format of author
 * @version 1.1 - incorrect format
 */
public final class InvalidAuthor {
}

/**
 * @author First Author (first@author.com)
 * @author second author has incorrect format
 * @author Third Author (third@author.com)
 * @version $Id$
 */
public final class TwoValidAndOneInvalidAuthor {
}

/**
 * @author first author has incorrect format
 * @author second author has incorrect format
 * @version $Id$
 */
public final class TwoInvalidAuthors {
}
