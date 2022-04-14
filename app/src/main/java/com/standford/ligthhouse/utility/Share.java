package com.standford.ligthhouse.utility;

import com.standford.ligthhouse.model.LinkModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Share {
    @NotNull
    public static ArrayList<LinkModel> interceptedLinks = new ArrayList<>();
    @NotNull
    public static LinkModel selectModel;
    @NotNull
    public static String name;
}
