package juloo.keyboard2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
Parse a key definition. The syntax for a key definition is:
- [(symbol):(payload)].
- (payload) can be single key or multiple keys that are comma separated,
  or even a string within single quotes.
  In case of any problems during parsing, that particular key is parsed as a string key.
 */
public final class KeyValueParser
{
  static public KeyValue parse(String str){
    String[] with_symbol = str.split(":",2);
    String symbol = null;
    String keydata;
    if(with_symbol.length == 1){
      keydata = str;
    }else{
      symbol = with_symbol[0];
      keydata = with_symbol[1];
    }
    String[] str_keys = escaped_split(keydata);

    if(symbol == null){
      if(str_keys.length == 1){
        if(str_keys[0].length() < 6){
          symbol = str_keys[0];
        }
        else{
          symbol = str_keys[0].substring(0,3)+"...";
        }
      }else{
        symbol = "μ["+ str_keys.length +"]";
      }
    }
    KeyValue[] keys = new KeyValue[str_keys.length];
    for (int i = 0; i < str_keys.length; i++) {
      keys[i] = KeyValue.getSpecialKeyByName(str_keys[i]);
      if(keys[i] == null){
        keys[i] = KeyValue.makeStringKey(str_keys[i]);
      }
    }
    return KeyValue.makeMacro(symbol,keys, KeyValue.FLAG_SMALLER_FONT);
  }

  // splits on comma (,)
  // escapes any char by backslash (\)
  // and all within single quotes (') is fully escaped.
  static private String[] escaped_split(String str){
    ArrayList<String> arl = new ArrayList<>();
    StringBuilder stb = new StringBuilder();
    boolean in_quotes = false;
    boolean to_escape = false;
    for (int i = 0; i < str.length(); i++) {
      if(str.charAt(i) == '\\' && !in_quotes){
        to_escape = true;
        continue;
      }
      if(str.charAt(i) == ',' && !in_quotes && !to_escape){
        arl.add(stb.toString());
        stb = new StringBuilder();
      }
      else if(str.charAt(i) == '\'' && !to_escape){
        in_quotes = !in_quotes;
      }
      else{
        stb.append(str.charAt(i));
      }
      to_escape = false;
    }
    if(stb.length() > 0){
      arl.add(stb.toString());
    }
    return arl.toArray(new String[0]);
  }
}
