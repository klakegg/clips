package net.klakegg.clips.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathBuilder {

    private List<String> parts;

    public PathBuilder() {
        parts = new ArrayList<>();
    }

    private PathBuilder(List<String> parts) {
        this.parts = parts;
    }

    public PathBuilder text(String p) {
        parts.add(p);

        return new PathBuilder(parts);
    }

    public String toString() {
        return parts.size() == 0 ? "" : "/" + parts.stream().collect(Collectors.joining("/"));
    }
}
