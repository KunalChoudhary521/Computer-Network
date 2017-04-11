clear all; clc;
%Part 2.2:

%---------------------------Blackbox 1 ----------------------------------
%blackbox1: b=3500(bits); R=0.37(Mbps); T=101_000(mu-sec)
%L=10000; N=1000;r=1000
file = 'unknown_bb1.txt';
[seq_num, send_time, recv_time] = textread(file, '%f %f %f');

time1 = 1:max(recv_time);
figure(1);
b1=3500; R1=0.37; T1=101000;
s_curve1 = b1 + (R1.* (time1 - T1));
plot(time1,s_curve1);
%ylim([0 max(s_curve1)*1.05]);
title('Service Curve: b1=3500(bits), R1=0.37(Mbps), T1=101000(micro-sec)');
ylabel('Packets (Bytes)');
xlabel('Time (microseconds)');

%---------------------------Blackbox 2 ----------------------------------
%blackbox2: b=3000(bits); R=0.05(Mbps); T=80(mu-sec)
file = 'unknown_bb2.txt';
[seq_num, send_time, recv_time] = textread(file, '%f %f %f');

time2 = 1:max(recv_time);
figure(2);
b2=3000; R2=0.05; T2=80;
s_curve2 = b2 + (R2.* (time2 - T2));
plot(time2,s_curve2);
title('Service Curve: b1=3000(bits), R1=0.05(Mbps), T1=80(micro-sec)');
ylabel('Packets (Bytes)');
xlabel('Time (microseconds)');


%---------------------------Blackbox 3 ----------------------------------
%blackbox3: b=1_900(bits); R=0.175(Mbps); T=75_000(mu-sec)
file = 'unknown_bb3.txt';
[seq_num, send_time, recv_time] = textread(file, '%f %f %f');

time3 = 1:max(recv_time);
figure(3);
b3=1900; R3=0.175; T3=75000;
s_curve3 = b3 + (R3.* (time3 - T3));
plot(time3,s_curve3);
title('Service Curve: b1=1900(bits), R1=0.175(Mbps), T1=75000(micro-sec)');
ylabel('Packets (Bytes)');
xlabel('Time (microseconds)');

