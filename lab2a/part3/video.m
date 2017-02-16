clc
clear all

%[seq_no_tr, time_tr, type_tr, size_tr, ~,~,~] = textread('movietrace.data', '%f %f %c %f %f %f %f');

%data of trace file
[seqNo,t_p,size_p] = textread('TGenVid.txt', '%f %f %f');

figure(1);
time_depart = zeros(1,size(seqNo,1));
Tgen_depart = zeros(1,size(seqNo,1));

t_p = t_p/1000;

time_depart(1) = t_p(1);
Tgen_depart(1) = size_p(1);
i = 2;
while i<=size(seqNo,1)
    time_depart(i) = time_depart(i - 1) + t_p(i);
    Tgen_depart(i) = Tgen_depart(i - 1) + size_p(i);
    i=i+1;
end
    
subplot(3,1,1);
plot(time_depart,Tgen_depart);
title('Traffic Generator - Trace');
xlabel('Time (ms)');
ylabel('Packet (bytes)');


%Arrivals at the token bucket
[recv_time, size_tb, buffer_bk_log, tokens] = textread('bucketVid.txt', '%f %f %f %f');
time_bucket = zeros(1,size(recv_time,1));
bucket_arrival = zeros(1,size(recv_time,1));

recv_time = recv_time/1000;

time_bucket(1) = recv_time(1);
bucket_arrival(1) = 0;

i = 2;
while i<(size(recv_time,1))
    time_bucket(i) = time_bucket(i - 1) + recv_time(i);
    bucket_arrival(i) = bucket_arrival(i - 1) + size_tb(i);
    i=i+1;
end

subplot(3,1,2);
plot(time_bucket,bucket_arrival);
title('Arrival At Token Bucket');
xlabel('time (ms)');
ylabel('Packet (bytes)');


%Arrivals at the Traffic Sink
[seqNo,t_p,size_p] = textread('TSinkVid.txt', '%f %f %f');

time_sink = zeros(1,size(seqNo,1));
sink_arrival = zeros(1,size(seqNo,1));

t_p = t_p/1000;

time_sink(1) = 0;
sink_arrival(1) = size_p(1);
i = 2;
while i<=size(seqNo,1)
    time_sink(i) = time_sink(i - 1) + t_p(i);
    sink_arrival(i) = sink_arrival(i - 1) + size_p(i);
    i=i+1;
end
    
subplot(3,1,3);
plot(time_sink,sink_arrival); 
title('Traffic Sink - Output');
xlabel('Time (ms)');
ylabel('Packet (bytes)');

%All 3 plots on 1 graph
figure(2);
subplot(2,1,1);
fig2 = plot(time_depart,Tgen_depart,'r',time_bucket,bucket_arrival,'g',time_sink,sink_arrival,'b');
hold on;
legend(fig2, 'Trace', 'Shaper', 'Sink');
title('Trace, Shaper & Sink Graphs');
xlabel('Time (ms)');
ylabel('Packet (bytes)');


%Shows contents of token bucket and buffer backlog as a function of time
figure(3);
subplot(2,1,1);
fig3 = plot(time_bucket,bucket_arrival,'r',time_bucket,buffer_bk_log,'b');
hold on;
legend(fig3,'Token Bucket', 'Back Log');
title('Token Bucket & Backlog');
xlabel('time (ms)');
ylabel('Packet (bytes)');
ylim([-0.5* max(bucket_arrival) 1.3*max(bucket_arrival)]);

