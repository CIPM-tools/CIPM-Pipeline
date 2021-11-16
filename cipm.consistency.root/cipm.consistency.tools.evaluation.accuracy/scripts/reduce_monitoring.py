from os import listdir
import json
import os

# function definition
def getAllServiceIds(kieker_folder):
    result = set()
    # read mapping
    with open(os.path.join(kieker_folder, "kieker.map")) as f:
        mapping_content = f.readlines()
    record_map_dict = {}
    for line in mapping_content:
        line_split = line.split("=")
        if len(line_split) == 2:
            record_map_dict[line_split[0]] = line_split[1]

    # read file
    for file in listdir(kieker_folder):
        if file.endswith(".dat"):
            data_file = os.path.join(kieker_folder, file)
            with open(data_file) as f:
                monitoring_data = f.readlines()
            for line in monitoring_data:
                data_split = line.split(";")
                record_type = data_split[0]
                if "cipm.consistency.bridge.monitoring.records.ServiceCallRecord" in record_map_dict[record_type]:
                    result.add(data_split[6])


    return result

# monitoring
def getServiceExecutionTimes(service_id, kieker_folder):
    result = []
    # read mapping
    with open(os.path.join(kieker_folder, "kieker.map")) as f:
        mapping_content = f.readlines()
    record_map_dict = {}
    for line in mapping_content:
        line_split = line.split("=")
        if len(line_split) == 2:
            record_map_dict[line_split[0]] = line_split[1]

    # read file
    for file in listdir(kieker_folder):
        if file.endswith(".dat"):
            data_file = os.path.join(kieker_folder, file)
            with open(data_file) as f:
                monitoring_data = f.readlines()
            for line in monitoring_data:
                data_split = line.split(";")
                record_type = data_split[0]
                if "cipm.consistency.bridge.monitoring.records.ServiceCallRecord" in record_map_dict[record_type]:
                    if service_id in data_split[6]:
                        # investigate it
                        l = len(data_split)
                        duration = (float(data_split[l - 1]) - float(data_split[l - 2])) / 1000000
                        result.append(duration)

    return result

# execution
data_base_path = "../test-data/opstime-monitoring-and-models/experiment-executions/"
data_prefix = "execution"
data_nums = range(1, 11, 1)

data_monitoring_path = "monitoring"
data_monitoring_all_path = "all"

for i in data_nums:
    out_all = []

    exec_path = data_base_path + data_prefix + str(i) + "/" + data_monitoring_path + "/"
    all_path = exec_path + data_monitoring_all_path

    monitoring_files = list(map(lambda x: os.path.join(all_path, x), listdir(all_path)))

    for mnfile in monitoring_files:
        out_map = {}

        allServiceIds = getAllServiceIds(mnfile)
        for idd in allServiceIds:
            vals = getServiceExecutionTimes(idd, mnfile)
            if idd in out_map:
                out_map[idd].extend(vals)
            else:
                out_map[idd] = vals

        out_all.append(out_map)

    with open(exec_path + "reduced.json", 'w') as outfile:
        json.dump(out_all, outfile)