clear all; clc;
%plot backlog, waiting time, # of packets discarded as a function of time for N=1

%Record arrival to file in following format:
%elapsed time (microseconds), packet size (bytes), backlog in buffers ordered by 
%index in array (bytes).

[elapsed_time, packet_size, backlog] = textread('ps_ex1-3-N1.txt', '%f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%Backlog as a Function of Time%%%%%%%%%%%%%%%%%%%%
figure(1);
time_depart = cumsum(elapsed_time);%cumsum = cumulative sum
%time_depart = time_depart / 1e6;%convert mu-sec -> sec

subplot(3,1,1);
plot(time_depart,backlog);
title('Backlog for N = 1');
xlabel('Time (microseconds)');
ylabel('Backlog (bytes)');

%%%%%%%%%%%%%%%%%%%%%%%%%%Waiting Time%%%%%%%%%%%%%%%%%%%%%%
[seq_num, arrival_time, sink_pkt_size] = textread('TSinkOut_ex1-3-N1.txt', '%f %f %f');
time_arrival = cumsum(arrival_time);

%  waiting_time = zeros(1,size(elapsed_time,1));
% i = 1;
% while i <= size(elapsed_time,1)
%     j = i;
%     while (((backlog(i) + packet_size(i)) > 102400) && j < size(elapsed_time,1))
%         j = j+1;
%     end
%     waiting_time(i) = abs(time_depart(i) - time_arrival(j));
%     i = i + 1;
% end
% 
% subplot(3,1,2);
% plot(time_arrival,waiting_time);
% title('Waiting Time for N = 1');
% xlabel('Time (microseconds)');
% ylabel('Backlog (bytes)');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Packets Discarded%%%%%%%%%%%%%%%%%%%%%%%%
discarded_packets = zeros(1,size(elapsed_time,1));
i = 1;

while  i <= size(sink_pkt_size,1)
    if((backlog(i) + sink_pkt_size(i)) > 102400)
        discarded_packets(i) = 1;
    end
    i = i + 1;
end

subplot(3,1,3);
plot(time_depart, cumsum(discarded_packets));
title('Discarded Packets for N = 1');
xlabel('Time (microseconds)');
ylabel('# of Packets');
