package com.ynthm.common.utils.tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树构建工具类
 *
 * @author Ethan Wang
 * @version 1.0
 */
public class TreeBuilder {
  private TreeBuilder() {}

  /**
   * 从ID/ParentID列表构建树
   *
   * @param items 所有节点数据
   * @param idGetter 获取节点ID的方法
   * @param parentIdGetter 获取父节点ID的方法
   * @param rootParentIds 根节点的父ID值（通常是null或0）
   * @param <T> 节点数据类型
   * @param <ID> ID类型
   * @return 树形结构根节点列表
   */
  public static <T, ID> List<TreeNode> buildTree(
      List<T> items,
      Function<T, ID> idGetter,
      Function<T, ID> parentIdGetter,
      Function<T, TreeNode> data2TreeNode,
      Set<ID> rootParentIds) {

    // 1. 创建所有节点
    Map<ID, TreeNode> nodeMap =
        items.stream()
            .collect(
                Collectors.toMap(idGetter, data2TreeNode, (existing, replacement) -> existing));

    // 2. 构建父子关系
    for (T item : items) {
      ID parentId = parentIdGetter.apply(item);
      TreeNode node = nodeMap.get(idGetter.apply(item));

      // 查找父节点
      TreeNode parent = null;
      if (parentId != null && !rootParentIds.contains(parentId)) {
        parent = nodeMap.get(parentId);
      }

      // 添加到父节点或根节点列表
      if (parent != null) {
        parent.addChild(node);
      }
    }

    // 3. 返回根节点列表
    return items.stream()
        .filter(
            item -> {
              ID parentId = parentIdGetter.apply(item);
              return rootParentIds.contains(parentId) || parentId == null;
            })
        .map(idGetter)
        .map(nodeMap::get)
        .collect(Collectors.toList());
  }

  /**
   * 高性能树构建（避免递归查找）
   *
   * @param items 所有节点数据
   * @param idGetter 获取节点ID的方法
   * @param parentIdGetter 获取父节点ID的方法
   * @param rootParentIds 根节点的父ID值
   * @param <T> 节点数据类型
   * @param <ID> ID类型
   * @return 树形结构根节点列表
   */
  public static <T, ID> List<TreeNode> buildTreeHighPerformance(
      List<T> items,
      Function<T, ID> idGetter,
      Function<T, ID> parentIdGetter,
      Function<T, TreeNode> data2TreeNode,
      Set<ID> rootParentIds) {

    // 创建ID到节点的映射
    Map<ID, TreeNode> nodeMap = new HashMap<>();
    // 创建ID到父ID的映射
    Map<ID, ID> parentIdMap = new HashMap<>();
    // 记录父节点到子ID列表的映射
    Map<ID, List<ID>> parentToChildrenMap = new HashMap<>();

    // 第一遍：准备数据
    for (T item : items) {
      ID id = idGetter.apply(item);
      ID parentId = parentIdGetter.apply(item);

      nodeMap.put(id, data2TreeNode.apply(item));
      parentIdMap.put(id, parentId);

      if (!rootParentIds.contains(parentId)) {
        parentToChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(id);
      }
    }

    // 第二遍：构建父子关系
    for (T item : items) {
      ID id = idGetter.apply(item);
      TreeNode node = nodeMap.get(id);

      List<ID> childIds = parentToChildrenMap.get(id);
      if (childIds != null) {
        for (ID childId : childIds) {
          node.addChild(nodeMap.get(childId));
        }
      }
    }

    // 返回根节点
    return items.stream()
        .filter(
            item -> {
              ID parentId = parentIdGetter.apply(item);
              return rootParentIds.contains(parentId) || parentId == null;
            })
        .map(idGetter)
        .map(nodeMap::get)
        .collect(Collectors.toList());
  }

  /**
   * 支持排序的树构建
   *
   * @param items 所有节点数据
   * @param idGetter 获取节点ID的方法
   * @param parentIdGetter 获取父节点ID的方法
   * @param rootParentIds 根节点的父ID值
   * @param comparator 子节点排序比较器
   * @param <T> 节点数据类型
   * @param <ID> ID类型
   * @return 排序后的树形结构
   */
  public static <T, ID> List<TreeNode> buildSortedTree(
      List<T> items,
      Function<T, ID> idGetter,
      Function<T, ID> parentIdGetter,
      Function<T, TreeNode> data2TreeNode,
      Set<ID> rootParentIds,
      Comparator<TreeNode> comparator) {

    List<TreeNode> roots = buildTree(items, idGetter, parentIdGetter, data2TreeNode, rootParentIds);
    sortTree(roots, comparator);
    return roots;
  }

  // 递归排序树
  private static void sortTree(List<TreeNode> nodes, Comparator<TreeNode> comparator) {
    if (nodes == null) return;

    nodes.sort(comparator);
    for (TreeNode node : nodes) {
      sortTree(node.getChildren(), comparator);
    }
  }
}
