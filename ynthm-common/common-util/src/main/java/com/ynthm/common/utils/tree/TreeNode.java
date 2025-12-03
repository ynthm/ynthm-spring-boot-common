package com.ynthm.common.utils.tree;

import java.util.List;

/**
 * @author Ethan Wang
 * @version 1.0
 */
public interface TreeNode {
  String getName();

  TreeNode getParent();

  List<TreeNode> getChildren();

  boolean addChild(TreeNode child);
}
