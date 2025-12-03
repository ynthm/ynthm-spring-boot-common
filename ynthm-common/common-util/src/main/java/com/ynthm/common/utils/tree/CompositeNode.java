package com.ynthm.common.utils.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 组合节点基类 (可包含子节点)
 *
 * @author Ethan Wang
 * @version 1.0
 */
public abstract class CompositeNode implements TreeNode {
  private String name;
  private final List<TreeNode> children = new ArrayList<>();
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
    return Collections.unmodifiableList(children);
  }

  @Override
  public boolean addChild(TreeNode child) {
    if (child instanceof CompositeNode) {
      CompositeNode compositeChild = (CompositeNode) child;
      compositeChild.parent = this;
    }

    return children.add(child);
  }
}
