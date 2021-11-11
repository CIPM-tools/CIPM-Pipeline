import json
import matplotlib.pyplot as plt
import statistics

with open('results_backward_raw.json', 'r') as infile:
    evaluation_results_backward = json.load(infile)
    
with open('results_forward_raw.json', 'r') as infile:
    evaluation_results_forward = json.load(infile)
    
v_forward = []
v_backward = []
for i in range(0, len(evaluation_results_forward["iterations"][0]["ws"])):
    iv = [evaluation_results_forward["iterations"][k]["ws"][i] for k in range(0, len(evaluation_results_forward["iterations"]))]
    v_forward.append(statistics.median(iv))
    
    ivb = [evaluation_results_backward["iterations"][k]["ws"][i] for k in range(0, len(evaluation_results_backward["iterations"]))]
    v_backward.append(statistics.median(ivb))
    
    
plt.plot(range(30,len(v_forward) * 5 + 30, 5),v_forward, color = "red", label="Forward prediction")
plt.plot(range(30,len(v_backward) * 5 + 30, 5),v_backward, color = "blue", label="Backward prediction")

plt.xlabel("Elapsed time in minutes")
plt.ylabel("Wasserstein distance between model simulation and monitoring")
plt.legend(loc="upper right")

axes = plt.gca()
axes.set_ylim([0,300])

plt.title('Wasserstein distance over time')
plt.savefig('ws_ot.png')