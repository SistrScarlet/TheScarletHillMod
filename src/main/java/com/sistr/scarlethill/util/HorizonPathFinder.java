package com.sistr.scarlethill.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.*;

import static com.sistr.scarlethill.util.VecMathUtil.getManhattan;

public class HorizonPathFinder {
    private final Set<Vec2i> map;
    private final Set<Node> openList = Sets.newHashSet();

    public HorizonPathFinder(Set<Vec2i> map) {
        this.map = map;
    }

    public Optional<List<Vec2i>> findPath(Vec2i start, Vec2i goal) {
        this.openList.clear();
        //map内にゴールを作る
        goal = getNearPos(goal, this.map);
        //最初のノードを作る
        Node startNode = new Node(start, 0, getManhattan(start, goal), null);
        this.openList.add(startNode);

        //探索処理
        while (true) {
            //最もコストの低いノードを選択
            Optional<Node> optional = this.openList.stream()
                    .filter(node -> node.getState() != Node.State.CLOSED)
                    .min(Comparator.comparingInt(Node::getScore));
            //openListにオープンなノードが無い場合
            //必ずマップ内にゴールが作られるため、まずありえない
            if (!optional.isPresent()) return Optional.empty();
            Node lowestNode = optional.get();
            lowestNode.setState(Node.State.CLOSED);

            //ゴールに着いた時の処理
            if (lowestNode.getPos().equals(goal)) {
                Node checkNode = lowestNode;
                List<Vec2i> path = Lists.newArrayList(checkNode.getPos());
                //ゴール地点のノードから親のノードを辿る
                while (checkNode.getParent().isPresent()) {
                    checkNode = checkNode.getParent().get();
                    path.add(checkNode.getPos());
                }
                Collections.reverse(path);
                return Optional.of(path);
            }

            //東西南北にノードを作れるかチェックして作る
            for (int i = 0; i < 4; i++) {
                Vec2i checkPos = lowestNode.getPos().offset(Direction.byHorizontalIndex(i));
                if (this.map.contains(checkPos) && this.openList.stream().noneMatch(node -> node.getPos() == checkPos)) {
                    Node onNode = new Node(checkPos, lowestNode.getActualCost() + 1, getManhattan(checkPos, goal), lowestNode);
                    this.openList.add(onNode);
                }
            }
        }
    }

    //map内の至近vecを求める。コードは単純なので説明ﾌﾖｫ!↑
    public static Vec2i getNearPos(Vec2i pos, Collection<Vec2i> map) {
        if (map.contains(pos)) return pos;
        Vec2i nearestPos = null;
        int distance = 0;
        for (Vec2i mapPos : map) {
            if (nearestPos == null) {
                nearestPos = mapPos;
                distance = getManhattan(nearestPos, pos);
            } else {
                int tempDist = getManhattan(mapPos, pos);
                if (tempDist < distance) {
                    nearestPos = mapPos;
                    distance = tempDist;
                }
            }
            if (distance == 0) {
                return nearestPos;
            }
        }
        return nearestPos;
    }

    static class Node {
        private final Vec2i pos;
        private final int actualCost;
        private final int expectedCost;
        @Nullable
        private final Node parent;
        private State state = State.NONE;

        public Node(Vec2i pos, int actualCost, int expectedCost, @Nullable Node parent) {
            this.pos = pos;
            this.actualCost = actualCost;
            this.expectedCost = expectedCost;
            this.parent = parent;
            this.setState(State.OPEN);
        }

        public Vec2i getPos() {
            return this.pos;
        }

        public void setState(State state) {
            this.state = state;
        }

        public State getState() {
            return this.state;
        }

        public int getActualCost() {
            return this.actualCost;
        }

        public int getExpectedCost() {
            return this.expectedCost;
        }

        public Optional<Node> getParent() {
            return Optional.ofNullable(this.parent);
        }

        public int getScore() {
            return this.getActualCost() + this.getExpectedCost();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return this.actualCost == node.actualCost &&
                    this.expectedCost == node.expectedCost &&
                    Objects.equals(this.pos, node.pos) &&
                    this.state == node.state;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.pos, this.state, this.actualCost, this.expectedCost);
        }

        enum State {
            NONE,
            OPEN,
            CLOSED
        }
    }
}
