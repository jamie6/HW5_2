/*
2.	Sieve of Eratosthenes
Print all the prime numbers under 1,000,000 using the Sieve of Eratosthenes algorithm.
a)	Solve using only one thread. Timestamp execution time
b)	Solve using Actors. Timestamp execution time.
c)	What is the speedup?
 */
package com.mycompany.cs3700_hw5_2;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractLoggingActor;
import akka.japi.pf.ReceiveBuilder;
/**
 *
 * @author Jamie
 */
public class HW5_2
{
    static int n;
    public static void main(String[] args)
    {
        n = 1000000;
        singleThreadTest(n);
        actorsTest(n);
    }
    
    public static void actorsTest(int n)
    {
        int threadCount = 4;
        int segment = n/threadCount;
        Object[] propsArgs = {n};
        ActorSystem system = ActorSystem.create("SoE");
        final ActorRef soe = system.actorOf(SieveOfEratosthenes.props(propsArgs), "SieveOfEratosthenes");

        for ( int i = 0; i < threadCount; i++ )
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    for ( int j = 0; j < segment; j++ )
                    {
                        soe.tell(new SieveOfEratosthenes.Message(), ActorRef.noSender());
                    }
                }
            }).start();
        }
        try
        {
        Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        system.terminate();
    }
    
    public static class SieveOfEratosthenes extends AbstractLoggingActor
    {
        long startTime, endTime;
        private int i = 2, n;
        private boolean[] a;
        StringBuilder sb = new StringBuilder();
        
        public SieveOfEratosthenes(int n)
        {
            this.n = n;
            a = new boolean[n+1];
            for ( int i = 0; i < a.length; i++ ) a[i] = true;
            startTime = System.currentTimeMillis();
        }
        
        @Override
        public Receive createReceive()
        {
            return ReceiveBuilder.create().match(Message.class, this::onMessage).build();
        }
        
        static class Message { }
        
        private void onMessage(Message message) throws Exception
        {
            if ( i <= Math.sqrt(n) && a[i] == true )
            {
                for ( int j = 0; i*i + j*i <= n; j++ )
                {
                    a[i*i + j*i] = false;
                }
            }
            if ( i < a.length && a[i] )
            {
                sb.append(i + " ");
                //log().info(Integer.toString(i));
            }
            i++;
            if ( i == a.length )
            {
                endTime = System.currentTimeMillis();
                log().info("Multithread Time: " + (endTime-startTime) + " ms.\n" + sb.toString());
            }
        }
        
        public static Props props(Object[] args)
        {
            return Props.create(SieveOfEratosthenes.class, args);
        }
    }
    
    public static void singleThreadTest(int n)
    {
        long startTime, endTime;
        StringBuilder sb = new StringBuilder();
        startTime = System.currentTimeMillis();
        boolean[] a = sieveOfEratosthenes(n);
        for ( int i = 2; i < n; i++ ) if ( a[i] ) sb.append(i).append(" ");
        endTime = System.currentTimeMillis();
        System.out.println("Single Thread Time: " + (endTime-startTime) + " ms.");
        System.out.println(sb.toString());
    }
    
    public static boolean[] sieveOfEratosthenes(int n)
    {
        boolean[] a = new boolean[n+1];
        for ( int i = 0; i < a.length; i++ ) a[i] = true;
        for ( int i = 2; i <= Math.sqrt(n); i++ )
        {
            if ( a[i] == true )
            {
                for ( int j = 0; i*i + j*i <= n; j++ )
                {
                    a[i*i + j*i] = false;
                }
            }
        }
        return a;
    }
}
