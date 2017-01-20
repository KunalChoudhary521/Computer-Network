clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[packet_no_p, time_p, packetsize_p] = textread('poisson3.data', '%f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%%%Exercise 1.2%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%Hint1: For Plot2 and Plot3, you only need to change 'initial_p', the
%       initial time in microseconds, and 'ag_frame', the time period of
%       aggregation
%Hint2: After adding Plot2 and Plot3 to this part, you can use 'subplot(3,1,2);'
%       and 'subplot(3,1,3);' respectively to show 3 plots in the same figure.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure(1);
jj=1;
i=1;
initial_p=0;
ag_time=1000000; %1s
bytes_p=zeros(1,100);
while time_p(jj)<=initial_p
    jj=jj+1;
end

while i<=100
    while ((time_p(jj)-initial_p)<=ag_time*i && jj<length(packetsize_p))
        bytes_p(i)=bytes_p(i)+packetsize_p(jj);
        
        jj=jj+1;
    end
    i=i+1;
end


%%%%%%%%
subplot(3,1,1);bar(bytes_p);
title('Bytes Arrived in 1s Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Note: Run the same MATLAB code for Exercise 1.3 and 1.4 but change the
%second line of the code in order to read the files 'poisson2.data' and
%'poisson3.data' respectively.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Plot 2 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
jj=1;
i=1;
initial_p=30;
ag_time=100000; %100ms
bytes_p=zeros(1,100);
while time_p(jj)<=initial_p
    jj=jj+1;
end
while i<=100
while ((time_p(jj)-initial_p)<=ag_time*i && jj<length(packetsize_p))
bytes_p(i)=bytes_p(i)+packetsize_p(jj);
jj=jj+1;
end
i=i+1;
end

t_p2 = [30:0.1:40-0.1];
%%%%%%%%
subplot(3,1,2);bar(t_p2, bytes_p);
xlim([30 42]);  %x-axis starts from 30 seconds, rather than 0
title('Bytes Arrived in 100ms Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes');
% Plot 3 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
jj=1;
i=1;
initial_p=50;
ag_time=10000; % 10ms
bytes_p=zeros(1,100);
while time_p(jj)<=initial_p
    jj=jj+1;
end
while i<=100
while ((time_p(jj)-initial_p)<=ag_time*i && jj<length(packetsize_p))
bytes_p(i)=bytes_p(i)+packetsize_p(jj);
jj=jj+1;
end
i=i+1;
end

t_p3 = [50.2:0.01:51.2-0.01];
%%%%%%%%
subplot(3,1,3);bar(t_p3, bytes_p);
xlim([50.2 51.4]);  %x-axis starts from 50.2 seconds, rather than 0
title('Bytes Arrived in 10ms Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes');


%%%%%%%%%%%%%%1.3: mean bit rate, mean & variance of time difference%%%%%%%%%

time_diff = time_p(2:length(time_p)) - time_p(1:length(time_p)-1);
mean_time_diff = mean(time_diff);
vari_time_diff = var(time_diff);

mean_bit_rate = ((mean(packetsize_p) * 8)/mean_time_diff) * 1e6;