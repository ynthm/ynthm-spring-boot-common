package com.ynthm.common.utils.tree;

import java.util.Collections;
import java.util.List;

/**
 * 叶子节点基类 (不可包含子节点)
 *
 * @author Ethan Wang
 * @version 1.0
 */
public abstract class LeafNode implements TreeNode {
  private String name;
  private CompositeNode parent;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TreeNode getParent() {
    return parent;
  }

  @Override
  public List<TreeNode> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public boolean addChild(TreeNode child) {
    throw new UnsupportedOperationException("Leaf nodes cannot have children: " + getName());
  }
}
