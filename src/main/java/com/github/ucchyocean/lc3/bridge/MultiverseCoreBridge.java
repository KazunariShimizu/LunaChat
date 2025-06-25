/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bridge;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import org.mvplugins.multiverse.core.MultiverseCore;

import java.nio.charset.StandardCharsets;

/**
 * MultiverseCore連携クラス
 * @author ucchy
 */
public class MultiverseCoreBridge {

    /** MultiverseCore API クラス */
    private MultiverseCoreBridge mvc;

    /** コンストラクタは使用不可 */
    private MultiverseCoreBridge() {
    }

    /*
     * MultiverseCore-apiをロードする
     * @param plugin MultiverseCoreのプラグインインスタンス
     * @param ロードしたかどうか
     */
    public static MultiverseCoreBridge load(Plugin plugin) {

        if ( plugin instanceof MultiverseCore ) {
            MultiverseCoreBridge bridge = new MultiverseCoreBridge();
            bridge.mvc = (MultiverseCoreBridge) plugin;
            return bridge;
        } else {
            return null;
        }
    }

    /**
     * 指定されたワールドのエイリアス名を取得する
     * @param worldName ワールド名
     * @return エイリアス名、取得できない場合はnullが返される
     */
    public String getWorldAlias(String worldName) {

        String mvworld = mvc.getWorldAlias(worldName);
        if ( mvworld != null ) {
            if (!mvworld.isEmpty()) {
                return mvworld;
            } else {
                return mvworld;
            }
        } else {
            return null;
        }
    }

    /**
     * 指定されたワールドのエイリアス名を取得する
     * @param world ワールド
     * @return エイリアス名、取得できない場合はnullが返される
     */
    public String getWorldAlias(World world) {

        String mvworld = mvc.getWorldAlias(world);
        if ( mvworld != null ) {
            if (!mvworld.isEmpty()) {
                return mvworld;
            } else {
                return mvworld;
            }
        } else {
            return null;
        }
    }
}
