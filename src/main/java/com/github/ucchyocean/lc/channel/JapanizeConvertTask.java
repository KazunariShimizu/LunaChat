/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.japanize.IMEConverter;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.japanize.YukiKanaConverter;
import com.github.ucchyocean.lc.member.ChannelMember;

/**
 * Japanize変換を実行して、実行後に発言を行うタスク
 * @author ucchy
 */
public class JapanizeConvertTask {

    private static final String REGEX_URL = "https?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+";

    private String org;
    private JapanizeType type;
    private Channel channel;
    private ChannelMember player;
    private String format;
    private String result;

    /**
     * コンストラクタ
     * @param org 変換前の文字列
     * @param type 変換タイプ
     * @param channel 変換後に発言する、発言先チャンネル
     * @param player 発言したプレイヤー
     * @param japanizeFormat 変換後に発言するときの、発言フォーマット
     */
    public JapanizeConvertTask(String org, JapanizeType type, Channel channel,
            ChannelMember player, String japanizeFormat) {
        this.org = org;
        this.type = type;
        this.channel = channel;
        this.player = player;
        this.format = japanizeFormat;
    }

    public void run() {
        runSync();
    }

    /**
     * 同期処理で変換を行います。結果は getResult() で取得してください。
     * @return 処理を実行したかどうか（イベントでキャンセルされた場合はfalseになります）
     */
    public boolean runSync() {

        // 変換対象外のキーワード
        HashMap<String, String> keywordMap = new HashMap<String, String>();
        ArrayList<String> keywords = new ArrayList<String>();
        if ( LunaChat.getConfig().isJapanizeIgnorePlayerName() ) {
//            for ( Player player : Bukkit.getOnlinePlayers() ) {
//                keywords.add(player.getName());
//            }
        }
        HashMap<String, String> dictionary =
                LunaChat.getAPI().getAllDictionary();

        // カラーコード削除、URL削除
        String deletedURL = Utility.stripColor(org.replaceAll(REGEX_URL, " "));

        // キーワードをロック
        int index = 0;
        String keywordLocked = deletedURL;
        for ( String keyword : keywords ) {
            if ( keywordLocked.contains(keyword) ) {
                index++;
                String key = "＜" + makeMultibytesDigit(index) + "＞";
                keywordLocked = keywordLocked.replace(keyword, key);
                keywordMap.put(key, keyword);
            }
        }
        for ( String dickey : dictionary.keySet() ) {
            if ( keywordLocked.contains(dickey) ) {
                index++;
                String key = "＜" + makeMultibytesDigit(index) + "＞";
                keywordLocked = keywordLocked.replace(dickey, key);
                keywordMap.put(key, dictionary.get(dickey));
            }
        }

        // カナ変換
        String japanized = YukiKanaConverter.conv(keywordLocked);

        // IME変換
        if ( type == JapanizeType.GOOGLE_IME ) {
            japanized = IMEConverter.convByGoogleIME(japanized);
        }

        // キーワードのアンロック
        for ( String key : keywordMap.keySet() ) {
            japanized = japanized.replace(key, keywordMap.get(key));
        }

        // 変換後の文字列にNGワードが含まれている場合は、マスクする
        for ( Pattern pattern : LunaChat.getConfig().getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(japanized);
            if ( matcher.find() ) {
                japanized = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
            }
        }

        // イベントコール
//        String channelName = (channel == null) ? "" : channel.getName();
//        EventResult event = LunaChat.getEventSender().sendLunaChatPostJapanizeEvent(
//                channelName, player, org, japanized);
//        if ( event.isCancelled() ) {
//            return false;
//        }
//        japanized = event.getValueAsString("japanized");

        // フォーマットする
        result = format.replace("%msg", org);
        result = result.replace("%japanize", japanized);

        return true;
    }

    /**
     * Japanize変換の結果を返します。
     * @return 変換結果
     */
    public String getResult() {
        return result;
    }

    /**
     * 数値を、全角文字の文字列に変換して返す
     * @param digit
     * @return
     */
    private String makeMultibytesDigit(int digit) {

        String half = Integer.toString(digit);
        StringBuilder result = new StringBuilder();
        for ( int index=0; index < half.length(); index++ ) {
            switch ( half.charAt(index) ) {
            case '0' : result.append("０"); break;
            case '1' : result.append("１"); break;
            case '2' : result.append("２"); break;
            case '3' : result.append("３"); break;
            case '4' : result.append("４"); break;
            case '5' : result.append("５"); break;
            case '6' : result.append("６"); break;
            case '7' : result.append("７"); break;
            case '8' : result.append("８"); break;
            case '9' : result.append("９"); break;
            }
        }
        return result.toString();
    }
}

