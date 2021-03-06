package org.talents.rql.filters;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.jazdw.rql.parser.ASTNode;
import net.jazdw.rql.parser.ASTVisitor;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ComparatorChain;

public class HeaderFilter<T> implements ASTVisitor<List<T>, List<T>> {

  protected static final String EMPTYSTRING = "";
  //public class ListFilter<T> implements ASTVisitor<List<T>, List<T>> {
  /* (non-Javadoc)
   * @see net.jazdw.rql.parser.ASTVisitor#visit(net.jazdw.rql.parser.ASTNode, java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> visit(ASTNode node, List<T> list) {
    switch (node.getName()) {
      case "and":
        for (Object obj : node) {
          if (obj instanceof ASTNode) {
            list = ((ASTNode) obj).accept(this, list);
          } else {
            throw new UnsupportedOperationException("Encountered a non-ASTNode argument in AND statement");
          }
        }
        return list;
      case "or":
        Set<T> set = new LinkedHashSet<T>();
        for (Object obj : node) {
          if (obj instanceof ASTNode) {
            set.addAll(((ASTNode) obj).accept(this, list));
          } else {
            throw new UnsupportedOperationException("Encountered a non-ASTNode argument in OR statement");
          }
        }
        return new ArrayList<>(set);
      case "eq":
      case "gt":
      case "ge":
      case "lt":
      case "le":
      case "ne":
        String propName = (String) node.getArgument(0);
        Object test = node.getArgumentsSize() > 1 ? node.getArgument(1) : null;

        List<T> result = new ArrayList<>();

        for (T item : list) {

          Object property = getProperty(item, propName);

          Comparable<Object> comparableProperty;
          if (property instanceof Comparable) {
            comparableProperty = (Comparable<Object>) property;
          } else {
            throw new UnsupportedOperationException(String.format("Property '%s' is not comparable", propName));
          }

          int comparisonValue;
          try {
            comparisonValue = comparableProperty.compareTo(test);
          } catch (ClassCastException e) {
            throw new UnsupportedOperationException(String.format("Couldn't compare '%s' to '%s'",
                property.toString(), test.toString()));
          }

          if (checkComparisonValue(node.getName(), comparisonValue)) {
            result.add(item);
          }
        }
        if(result.size()>0)
          return list;
        return result;
      case "like":
      case "match":
        propName = (String) node.getArgument(0);
        String matchString = (String) node.getArgument(1);
        Pattern matchPattern = Pattern.compile(matchString.replace("*", ".*"),
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        result = new ArrayList<>();

        for (T item : list) {

          Object property = getProperty(item, propName);

          String stringProperty;
          if (property instanceof String) {
            stringProperty = (String) property;
          } else {
            throw new UnsupportedOperationException(String.format("Property '%s' is not a string", propName));
          }

          if (matchPattern.matcher(stringProperty).matches()) {
            result.add(item);
          }
        }
        if(result.size()>0)
          return list;
        return result;
      case "limit":
        int limit = (int) node.getArgument(0);
        int offset = node.getArgumentsSize() > 1 ? (int) node.getArgument(1) : 0;

        if (offset > list.size() - 1) {
          return Collections.emptyList();
        }

        int toIndex = offset + limit;
        if (toIndex > list.size()) {
          toIndex = list.size();
        }

        return list.subList(offset, toIndex);
      case "sort":
        ComparatorChain cc = new ComparatorChain();
        for (Object obj : node) {
          String sortOption = (String) obj;
          boolean desc = sortOption.startsWith("-");
          cc.addComparator(new BeanComparator<T>(sortOption.substring(1)), desc);
        }
        // copy the list as we are modifying it
        list = new ArrayList<>(list);
        Collections.sort(list, cc);
        return list;
      default:
        throw new UnsupportedOperationException(String.format("Encountered unknown operator '%s'", node.getName()));
    }
  }

  private boolean checkComparisonValue(String name, int value) {
    switch (name) {
      case "eq":
        return value == 0;
      case "gt":
        return value > 0;
      case "ge":
        return value >= 0;
      case "lt":
        return value < 0;
      case "le":
        return value <= 0;
      case "ne":
        return value != 0;
    }
    return false;
  }

  private Object getProperty(Object item, String propName) throws UnsupportedOperationException {
    Object property;
    try {
      String key = (String)PropertyUtils.getProperty(item, "key");
      if(key.compareToIgnoreCase(propName) == 0)
        return (String)PropertyUtils.getProperty(item, "value");
      else
        return EMPTYSTRING;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new UnsupportedOperationException(String.format("Could not retrieve property '%s' from list object", propName));
    }
  }
}


