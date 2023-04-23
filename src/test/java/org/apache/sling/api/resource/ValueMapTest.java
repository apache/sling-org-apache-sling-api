package org.apache.sling.api.resource;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class ValueMapTest {
    
    
    
    private static final String CALENDAR_STRING = "Fri Aug 19 16:02:37 EST 2022"; // 21:02:37 UTC



    @Test
    public void testCalendarConversion() throws ParseException {
        MemoryValueMap vm = new MemoryValueMap();
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        cal.setTime(sdf.parse(CALENDAR_STRING));

        vm.put("calendar",cal);
        String value = vm.get("calendar", String.class);
        assertEquals(CALENDAR_STRING, value);
    }
    
    
    
    private static class MemoryValueMap implements ValueMap {
        
        Map<String,Object> store = new HashMap<>();

        @Override
        public int size() {
            return store.size();
        }

        @Override
        public boolean isEmpty() {
            return store.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return store.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return store.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            return store.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            return store.remove(key);
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            store.putAll(m);
            
        }

        @Override
        public void clear() {
            store.clear();
            
        }

        @Override
        public Set<String> keySet() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<Object> values() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    

}
