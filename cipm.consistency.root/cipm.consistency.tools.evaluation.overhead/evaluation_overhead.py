import json
import statistics

def toLatex(vs, start = 0, step = 5):
    s = ""
    p = start
    for v in vs:
        s = s + "(" + str(int(p)) + "," + str(float(v)) + ")"
        p += step
    return s

data_base_path = "../cipm.consistency.tools.evaluation.accuracy/test-data/opstime-monitoring-and-models/experiment-executions/"
data_prefix = "execution"
data_nums = range(1, 11, 1)
data_overhead = "overhead/overhead.json"

overhead_ot = []

for i in data_nums:
    tss = [] # all overhead intervals

    json_file = data_base_path + data_prefix + str(i) + "/" + data_overhead
    with open(json_file) as json_fhandle:
        data = json.load(json_fhandle)
        # process data
        for v in data:
            ts_start = v["timestamp"]
            ts_end = v["endTimestamp"]
            sum_overhead = 0

            mp = v["nanoOverheadMap"]
            for kk in mp:
                sum_overhead += mp[kk]

            tss.append((ts_start, ts_end, sum_overhead))

    min_ts = 0
    max_ts = max([k[1] for k in tss])
    
    ov_sum = []
    ts_interval = 1000 * 60 * 5 # 5 minutes
    max_pointer = min_ts + 1000 * 60 * 180 # 180 minutes
    pointer = min_ts + ts_interval
    while (pointer <= max_pointer):
        ival_sum = 0
        ivalstart = pointer - ts_interval
        ivalend = pointer
        for ts in tss:
            if ts[0] >= ivalstart and ts[1] <= ivalend:
                ival_sum += ts[2]
            elif ts[0] < ivalend and ts[0] >= ivalstart and ts[1] > ivalend:
                part = (ivalend - ts[0]) / (ts[1] - ts[0])
                ival_sum += ts[2] * part
            elif ts[0] < ivalstart and ts[1] > ivalstart and ts[1] <= ivalend:
                part = (ts[1] - ivalstart) / (ts[1] - ts[0])
                ival_sum += ts[2] * part
            elif ts[0] < ivalstart and ts[1] > ivalend:
                part = ts_interval / (ts[1] - ts[0])
                ival_sum += ts[2] * part
                
        ov_sum.append(ival_sum)
        pointer += ts_interval

    overhead_ot.append(ov_sum)

overhead_ot_med = [statistics.median([overhead_ot[k][i] for k in range(0, len(overhead_ot))]) for i in range(0, len(overhead_ot[0]))]
overhead_ot_med = [(i / 1000000000) for i in overhead_ot_med]

# output data
import matplotlib.pyplot as plt

plt.plot(range(0, len(overhead_ot_med) * 5, 5), overhead_ot_med, color = "red")
plt.xlabel("Elapsed time in minutes")
plt.ylabel("Monitoring overhead in seconds")
plt.show()

with open('result.json', 'w') as outfile:
    json.dump(overhead_ot_med, outfile)

print(toLatex(overhead_ot_med))
