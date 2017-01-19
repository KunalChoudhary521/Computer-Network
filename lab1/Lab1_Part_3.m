clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading the data and putting the first 100000 entries in variables 
%Note that time is in seconds and framesize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[time1, framesize1] = textread('BC-pAug89.Tl', '%f %f');
no_entries = length(time1);
time=time1(1:no_entries);
framesize=framesize1(1:no_entries);

%%%%%%%%%%%%%%%%%%%%%%%%%Exercise %%%3.2%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

figure();
subplot(2,1,1); plot(time, framesize);
xlabel('Time')
ylabel('Packet Size in Bytes')

subplot(2,1,2); hist(framesize, length(framesize));
xlabel('Frame Size')
ylabel('Frame Count')

%%%%%%%%%%%%%%%%%%%%%%%%%Exercise %%%3.3%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure();
jj=1;
i=1;
initial_p=0;
ag_time=1;
bytes_p=zeros(1,100);

current_bit_rate = 0;
peak_bit_rate = 0;

while time(jj)<=initial_p
    jj=jj+1;    
end

while i<=100
    while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)  
        if jj > 1
            current_bit_rate = (framesize(jj) * 8) / (time(jj) - time(jj-1));
            if current_bit_rate > peak_bit_rate
                peak_bit_rate = current_bit_rate;
            end
        end
        bytes_p(i)=bytes_p(i)+framesize(jj);
        jj=jj+1;
    end
    i=i+1;
end

subplot(3,1,1);bar(bytes_p);
title('Bytes Arrived in 1s Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes Arrived');
%%%%%%%%


total_packets = length(framesize)
total_bytes = sum(framesize)
total_time = time(end) - time(1);

mean_bit_rate = (total_bytes * 8) / total_time
peak_bit_rate

peak_to_average_ratio = peak_bit_rate / mean_bit_rate

% 3.3 Plot 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
jj=1;
i=1;
initial_p=20; %20s
ag_time=0.1; %100ms
bytes_p=zeros(1,100);


while time(jj)<=initial_p
    jj=jj+1;    
end

while i<=100
    while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)  
        bytes_p(i)=bytes_p(i)+framesize(jj);
        jj=jj+1;
    end
    i=i+1;
end

subplot(3,1,2);bar(bytes_p);
title('Bytes Arrived in 100ms Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes Arrived');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% 3.3 Plot 3
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
jj=1;
i=1;
initial_p=90; %90s
ag_time=0.01; %10ms
bytes_p=zeros(1,100);

while time(jj)<=initial_p
    jj=jj+1;    
end

while i<=100
    while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)          
        bytes_p(i)=bytes_p(i)+framesize(jj);
        jj=jj+1;
    end
    i=i+1;
end
subplot(3,1,3);bar(bytes_p);
title('Bytes Arrived in 10ms Intervals');
xlabel('Interval Number');
ylabel('Number of Bytes Arrived');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
