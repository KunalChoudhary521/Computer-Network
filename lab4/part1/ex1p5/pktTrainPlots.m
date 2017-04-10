clear all; clc;


output1 = 'output1.txt';%N=100; L=400; r=10
[seqNo, send_time, recv_time] = textread(output1, '%f %f %f');
%time_diff_1 = recv_time - send_time;

figure(1);
plot(seqNo, send_time, seqNo, recv_time);
title('Packet train For N=100, L=400, r=10');
xlabel('Sequence Numbers');
ylabel('Time (microseconds)');
legend('Send','Receive');


output2 = 'output2.txt';%N=100; L=400; r=1000
[seqNo, send_time, recv_time] = textread(output2, '%f %f %f');
%time_diff_2 = recv_time - send_time;

figure(2);
plot(seqNo, send_time, seqNo, recv_time);
title('Packet train For N=100, L=400, r=1000');
xlabel('Sequence Numbers');
ylabel('Time (microseconds)');
legend('Send','Receive');

output3 = 'output3.txt';%N=100; L=400; r=10000
[seqNo, send_time, recv_time] = textread(output3, '%f %f %f');
%time_diff_3 = recv_time - send_time;

figure(3);
plot(seqNo, send_time, seqNo, recv_time);
title('Packet train For N=100, L=400, r=10000');
xlabel('Sequence Numbers');
ylabel('Time (microseconds)');
legend('Send','Receive');