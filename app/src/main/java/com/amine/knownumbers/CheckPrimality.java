package com.amine.knownumbers;

public class CheckPrimality {

    public static boolean millerRabin(long numb) {
        if (numb < 2)
            return false;

        int r = 0;
        long d = numb - 1;
        while ((d & 1) == 0) {
            d >>= 1;
            r++;
        }

        for (int a : new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
            if (numb == a)
                return true;
            if (check_composite(numb, a, d, r))
                return false;
        }
        return true;
    }
    private static boolean check_composite(long n, long a, long d, int s) {
        long x = binPower(a, d, n);
        if (x == 1 || x == n - 1)
            return false;
        for (int r = 1; r < s; r++) {
            //x = (u128)x * x % n;
            x = x * x % n;
            if (x == n - 1)
                return false;
        }
        return true;
    }
    private static long binPower(long base, long e, long mod) {
        long result = 1;
        base %= mod;
        while (e > 0) {
            if ((e & 1) > 0) {
                //result = (u128) result * base % mod;
                result =  result * base % mod;
            }
            //base = (u128)base * base % mod;
            base = base * base % mod;
            e >>= 1;
        }
        return result;
    }
}
