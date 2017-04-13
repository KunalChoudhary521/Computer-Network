clear all; clc;

L=2000000;
N=40000;
file = 'output_bb3.txt';
[seq_num, send_time, recv_time] = textread(file, '%f %f %f');

packets = seq_num.*(L/N);
% figure(1);
% plot(send_time,packets, recv_time,packets); 
% title('Arrival & Departure Functions');
% ylabel('Culmulative Packets (Bytes)');
% xlabel('Time (microseconds)');
% legend('A(t)','D(t)');

%find maximum backlog
%1  Count # of timestamps of recv_time that are lower than ith send_time
%2  multiple (L/N) by # of such send_times
%3  find max of this value to get max backlog

i = 1;j = 1;
Bmax1 = 0;
currBLog = 0;
while i <= size(send_time,1)
    count = recv_time<=send_time(i);%1
    numElements = sum(count);    
    currBLog = packets(i) - ((L/N) * numElements);%2
    if(Bmax1 < currBLog)
        Bmax1 = currBLog;%3
        currSendTs = send_time(i);
    end
    i = i + 1;
end

%--------------------Repeated Experiment (same parameters)------------
file = 'output_bb3_ex2.txt';
[seq_num2, send_time2, recv_time2] = textread(file, '%f %f %f');

packets = seq_num2.*(L/N);

i = 1;j = 1;
Bmax2 = 0;
currBLog2 = 0;
while i <= size(send_time2,1)
    count = recv_time2<=send_time2(i);%1
    numElements = sum(count);    
    currBLog2 = packets(i) - ((L/N) * numElements);%2
    if(Bmax2 < currBLog2)
        Bmax2 = currBLog2;%3
        currSendTs2 = send_time2(i);
    end
    i = i + 1;
end
%--------------------------------------------------------------------
%--------------------Repeated Experiment (2x parameters)------------
file = 'output_bb3_ex3.txt';
[seq_num2, send_time3, recv_time3] = textread(file, '%f %f %f');

L=4000000;
N=80000;
packets = seq_num2.*(L/N);

i = 1;j = 1;
Bmax3 = 0;
currBLog3 = 0;
while i <= size(send_time3,1)
    count = recv_time3<=send_time3(i);%1
    numElements = sum(count);    
    currBLog3 = packets(i) - ((L/N) * numElements);%2
    if(Bmax3 < currBLog3)
        Bmax3 = currBLog3;%3
        currSendTs3 = send_time3(i);
    end
    i = i + 1;
end
%--------------------------------------------------------------------

%Exact & Estimated service curves:
exact_S = (1480*8) + (1000.* (recv_time - 20000));
est_S1 = (1000 .* recv_time) - Bmax1;
est_S2 = (1000 .* recv_time) - Bmax2;
est_S3 = (1000 .* recv_time) - Bmax3;

%Graphing all service curves:
figure(2);
plot(recv_time,exact_S,'r',recv_time,est_S1,'g',recv_time2,est_S2,'b',recv_time2,est_S2,'m');
title('Service Curves: L=2,000,000 bytes, N=40,000');
ylabel('Packets (Bytes)');
xlabel('Time (microseconds)');
legend('Exact S(t)','Exp#1 S(t)','Exp#2 S(t)','2x-param S(t)');
