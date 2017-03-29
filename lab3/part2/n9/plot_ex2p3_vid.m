clear all; clc;
%plot backlog, waiting time, # of packets discarded as a function of time for N=1

%Record arrival to packet scheduler file in following format:
%elapsed time (microseconds), packet size (bytes), backlog in buffers ordered by 
%index in array (bytes).

schdfile = 'ps.txt';
sinkfile = 'TSinkVid.txt';

[elapsed_time, packet_size, backlog_vid, ~,priority] = textread(schdfile, '%f %f %f %f %f');
remove_idx = find(priority == 1);%remove data related to poisson3
elapsed_time(remove_idx) = [];
packet_size(remove_idx) = [];
backlog_vid(remove_idx) = [];

%%%%%%%%%%%%%%%%%%%%%%%Backlog as a Function of Time%%%%%%%%%%%%%%%%%%%%
figure(2);
%time_depart is an array of times when packet left packet scheduler
time_depart = cumsum(elapsed_time);%cumsum = cumulative sum
%time_depart = time_depart / 1e6;%convert mu-sec -> sec

subplot(3,1,1);
plot(time_depart,backlog_vid);
title('Video''s Backlog for N = 9');
xlabel('Time (microseconds)');
ylabel('Backlog (bytes)');

%%%%%%%%%%%%%%%%%%%%%%%%%%Waiting Time%%%%%%%%%%%%%%%%%%%%%%
[seq_num, arrival_time, sink_pkt_size] = textread(sinkfile, '%f %f %f');
%time_arrival is an array of times when packet arrived at traffic sink
time_arrival = cumsum(arrival_time);

waiting_time = zeros(1,size(arrival_time,1));
i = 1;
while i <= size(arrival_time,1)
    waiting_time(i) = time_depart(i) - time_arrival(i);%time_arrival(i) - time_depart(i);
    i = i + 1;
end

subplot(3,1,2);
plot(time_arrival,waiting_time);
title('Video''s Waiting Time for N = 9');
xlabel('Time (microseconds)');
ylabel('Wait Time (microseconds)');
%ylim([0  max(waiting_time)*1.2]);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Packets Discarded%%%%%%%%%%%%%%%%%%%%%%%%
discarded_packets = zeros(1,size(elapsed_time,1));
i = 1;

while  i <= size(packet_size,1)
    if((backlog_vid(i) + packet_size(i)) > 102400)
        discarded_packets(i) = 1;
    end
    i = i + 1;
end

subplot(3,1,3);
plot(time_depart, cumsum(discarded_packets));
title('Video''s Discarded Packets for N = 9');
xlabel('Time (microseconds)');
ylabel('# of Packets');


%these packets sum to 100*1024: sum(packet_size(1:1016),1) (at this point
%buffer overflows

%at this time, buffer overflows: sum(elapsed_time(1:1016),1)

% slope of waiting_time vs time plot: 
%slope = (waiting_time(end) - waiting_time(1))/(time_arrival(end) - time_arrival(1))