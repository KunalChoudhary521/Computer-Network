

[seq_no_p, t_p, size_p] = textread('poisson3.data', '%f %f %f');%info from poisson3.data

seq_no_s = zeros(10000,1);
[seq_no_s, t_s, size_s] = textread('TSinkout.txt', '%f %f %f');%info from Traffic Sink (TSinkout.txt)

figure(1);
subplot(3,1,1); bar(seq_no_p,size_p);%hist(size_p-size_s,3);
title('poisson3.data Packets');
ylabel('Packets Size (bytes)');
xlabel('Sequence Number');
xlim([9000 10050]);

subplot(3,1,2); bar(seq_no_s,size_s);
title('TSinkout.txt Packets');
ylabel('Packets Size (bytes)');
xlabel('Sequence Number');
xlim([9000 10050]);