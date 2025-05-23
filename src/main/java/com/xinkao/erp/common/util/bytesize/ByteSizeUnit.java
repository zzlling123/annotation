package com.xinkao.erp.common.util.bytesize;

/**
 ** @ClassName: ByteSizeUnit
 ** @Description: 字节单位
 ** @author: yongshun.han
 ** @date: 2020年1月15日 上午9:14:43
 **/
public enum ByteSizeUnit {
    BYTES {
        @Override
        public long toBytes(long size) {
            return size;
        }
        
        @Override
        public long toKB(long size) {
            return size / (C1 / C0);
        }
        
        @Override
        public long toMB(long size) {
            return size / (C2 / C0);
        }
        
        @Override
        public long toGB(long size) {
            return size / (C3 / C0);
        }
        
        @Override
        public long toTB(long size) {
            return size / (C4 / C0);
        }
        
        @Override
        public long toPB(long size) {
            return size / (C5 / C0);
        }
    },
    KB {
        @Override
        public long toBytes(long size) {
            return x(size, C1 / C0, MAX / (C1 / C0));
        }
        
        @Override
        public long toKB(long size) {
            return size;
        }
        
        @Override
        public long toMB(long size) {
            return size / (C2 / C1);
        }
        
        @Override
        public long toGB(long size) {
            return size / (C3 / C1);
        }
        
        @Override
        public long toTB(long size) {
            return size / (C4 / C1);
        }
        
        @Override
        public long toPB(long size) {
            return size / (C5 / C1);
        }
    },
    MB {
        @Override
        public long toBytes(long size) {
            return x(size, C2 / C0, MAX / (C2 / C0));
        }
        
        @Override
        public long toKB(long size) {
            return x(size, C2 / C1, MAX / (C2 / C1));
        }
        
        @Override
        public long toMB(long size) {
            return size;
        }
        
        @Override
        public long toGB(long size) {
            return size / (C3 / C2);
        }
        
        @Override
        public long toTB(long size) {
            return size / (C4 / C2);
        }
        
        @Override
        public long toPB(long size) {
            return size / (C5 / C2);
        }
    },
    GB {
        @Override
        public long toBytes(long size) {
            return x(size, C3 / C0, MAX / (C3 / C0));
        }
        
        @Override
        public long toKB(long size) {
            return x(size, C3 / C1, MAX / (C3 / C1));
        }
        
        @Override
        public long toMB(long size) {
            return x(size, C3 / C2, MAX / (C3 / C2));
        }
        
        @Override
        public long toGB(long size) {
            return size;
        }
        
        @Override
        public long toTB(long size) {
            return size / (C4 / C3);
        }
        
        @Override
        public long toPB(long size) {
            return size / (C5 / C3);
        }
    },
    TB {
        @Override
        public long toBytes(long size) {
            return x(size, C4 / C0, MAX / (C4 / C0));
        }
        
        @Override
        public long toKB(long size) {
            return x(size, C4 / C1, MAX / (C4 / C1));
        }
        
        @Override
        public long toMB(long size) {
            return x(size, C4 / C2, MAX / (C4 / C2));
        }
        
        @Override
        public long toGB(long size) {
            return x(size, C4 / C3, MAX / (C4 / C3));
        }
        
        @Override
        public long toTB(long size) {
            return size;
        }
        
        @Override
        public long toPB(long size) {
            return size / (C5 / C4);
        }
    },
    PB {
        @Override
        public long toBytes(long size) {
            return x(size, C5 / C0, MAX / (C5 / C0));
        }
        
        @Override
        public long toKB(long size) {
            return x(size, C5 / C1, MAX / (C5 / C1));
        }
        
        @Override
        public long toMB(long size) {
            return x(size, C5 / C2, MAX / (C5 / C2));
        }
        
        @Override
        public long toGB(long size) {
            return x(size, C5 / C3, MAX / (C5 / C3));
        }
        
        @Override
        public long toTB(long size) {
            return x(size, C5 / C4, MAX / (C5 / C4));
        }
        
        @Override
        public long toPB(long size) {
            return size;
        }
    };
    
    static final long C0 = 1L;
    static final long C1 = C0 * 1024L;
    static final long C2 = C1 * 1024L;
    static final long C3 = C2 * 1024L;
    static final long C4 = C3 * 1024L;
    static final long C5 = C4 * 1024L;
    
    static final long MAX = Long.MAX_VALUE;
    
    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    static long x(long d, long m, long over) {
        if (d > over) {
            return Long.MAX_VALUE;
        }
        if (d < -over) {
            return Long.MIN_VALUE;
        }
        return d * m;
    }
    
    public abstract long toBytes(long size);
    
    public abstract long toKB(long size);
    
    public abstract long toMB(long size);
    
    public abstract long toGB(long size);
    
    public abstract long toTB(long size);
    
    public abstract long toPB(long size);
}
