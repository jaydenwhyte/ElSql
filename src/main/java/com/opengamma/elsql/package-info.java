/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

/**
 * A library to manage SQL external to the application.
 * <p>
 * This package contains a standalone library for managing SQL.
 * <p>
 * There are a number of techniques for creating SQL within an application.
 * The main ones are:
 * <ul>
 * <li>an Object Relational Mapper framework, such as Hibernate
 * <li>appending Strings, such as sql = "SELECT foo " + "FROM bar " + "WHERE ..."
 * <li>using a fluent API library, with methods like select("foo").from("bar").where(..)
 * <li>reading in an external file, such as a properties file
 * </ul>
 * This library focuses on the last of these, using an external file.
 * It is designed for use with Spring, and uses the Spring SqlParameterSource class.
 * <p>
 * The key benefit is a simple external file that a DBA can understand, something
 * which is invaluable for later maintenance and debugging.
 * <p>
 * The file format is a file which typically has the suffix ".elsql".
 * Here is an example highlighting the structure:
 * <pre>
 *  -- an example comment
 *  &#064;NAME(SelectBlogs)
 *    SELECT &#064;INCLUDE(CommonFields)
 *    FROM blogs
 *    WHERE id = :id
 *      &#064;AND(:date)
 *        date &gt; :date
 *  &#064;NAME(CommonFields)
 *    title, author, content
 * </pre>
 * <ul>
 * <li>two dashes are used for comments
 * <li>tags start with the @ symbol
 * <li>the primary blocks are &#064;NAME(name) - the name refers to the block
 * <li>indentation is used to create blocks - indented lines "belong" to the parent less-indented line
 * <li>variables start with a colon, as this is integrated with Spring
 * <li>the various tags aim to handle over 80% of your needs
 * </ul>
 * <p>
 * These are the tags:
 * <p>
 * &#064;NAME(name)<br>
 * The name tag creates a named block which can be referred to from the application
 * or another part of the elsql file. The tag must be on a line by itself.
 * <p>
 * &#064;INCLUDE(nameOrVariable)<br>
 * The include tag includes the contents of a named block or a variable (prefixed by colon).
 * The tag may be embedded in the middle of a line.
 * <p>
 * &#064;WHERE<br>
 * The where tag works together with the and/or tags to build dynamic searches.
 * The tag will output an SQL WHERE, but only if there is at least some content output from the block.
 * Normally, the where tag is not needed, as there is typically always one active where clause.
 * The where tag must be on a line by itself.
 * <p>
 * &#064;AND(expression), &#064;OR(expression)<br>
 * These tags are equivalent and output SQL AND or OR.
 * The block that the tag contains is only output if the expression is true.
 * The output SQL will avoid outputting the AND or OR if it immediately follows a WHERE.
 * The and/or tag must be on a line by itself.
 * <p>
 * The expression is evaluated as follows.
 * If the variable does not exist, then the result is false.
 * Otherwise, if the expression is (:foo) and foo is a boolean, then the result is the boolean value.
 * Otherwise, if the expression is (:foo) and foo is not a boolean, then the result is true.
 * Otherwise, if the expression is (:foo = bar) then the result is true if the variable equals "bar" ignoring case.
 * <p>
 * &#064;LIKE sqlWithVariable<br>
 * &#064;LIKE(variable)<br>
 * The like tag adds either an SQL = or an SQL LIKE based on the specified variable.
 * If the tag has no variable in brackets, then the text between the like tag and the end
 * of the line is parsed for a variable.
 * This tag can differ by database, so the actual SQL is generated by the configuration class.
 * <p>
 * &#064;ENDLIKE<br>
 * The end-like tag is used on rare occasions to scope the end of the like tag.
 * Normally, the SQL should be written such that the end of the like tag is the end of the line.
 * <p>
 * &#064;EQUALS sqlWithVariable<br>
 * &#064;EQUALS(variable)<br>
 * The equals tag adds either an SQL = or an SQL IS NULL based on the specified variable.
 * If the tag has no variable in brackets, then the text between the equals tag and the end
 * of the line is parsed for a variable.
 * <p>
 * &#064;ENDEQUALS<br>
 * The end-equals tag is used on rare occasions to scope the end of the equals tag.
 * Normally, the SQL should be written such that the end of the equals tag is the end of the line.
 * <p>
 * &#064;PAGING(offsetVariable,fetchVariable)<br>
 * The paging tag adds the SQL code to page the results of a search.
 * These can differ by database, so the actual SQL is generated by the configuration class.
 * The tag bases its actions on the specified integer variables which should begin with a colon.
 * This replaces the OFFSETFETCH/FETCH tags in most situations as it enables window functions
 * to be used where necessary.
 * <p>
 * &#064;OFFSETFETCH<br>
 * &#064;OFFSETFETCH(offsetVariable,fetchVariable)<br>
 * The offset-fetch tag adds the SQL OFFSET and FETCH clauses for paging results.
 * These can differ by database, so the actual SQL is generated by the configuration class.
 * The tag bases its actions on the specified integer variables which should begin with a colon.
 * The names "paging_offset" and "paging_fetch" are used if the variables are not specified.
 * <p>
 * &#064;FETCH(fetchVariable)<br>
 * The fetch tag adds the SQL FETCH clause. It works as per the offset-fetch tag.
 */
package com.opengamma.elsql;
