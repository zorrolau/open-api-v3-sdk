//
// Created by zxp on 17/07/18.
//

#ifndef CPPSDK_UTILS_H
#define CPPSDK_UTILS_H

#include <ctime>
#include <string>
#include <map>

char * GetTimestamp(char *timestamp, int len);
std::string BuildParams(std::string requestPath, std::map<std::string,std::string> m);
std::string JsonFormat(std::string jsonStr);

#endif //CPPSDK_UTILS_H
