def plus(X, Y):
    fx = float(X)
    fy = float(Y)
    return str(fx + fy)


def minus(X, Y):
    fx = float(X)
    fy = float(Y)
    return str(fx - fy)


def times(X, Y):
    fx = float(X)
    fy = float(Y)
    return str(fx * fy)


def divide(X, Y):
    fx = float(X)
    fy = float(Y)
    return str(fx / fy)


def greater(X, Y):
    fx = float(X)
    fy = float(Y)
    return (fx > fy)


def smaller(X, Y):
    fx = float(X)
    fy = float(Y)
    return (fx < fy)


def equal(X, Y):
    fx = float(X)
    fy = float(Y)
    return (fx == fy)


def greaterOrEqual(X, Y):
    fx = float(X)
    fy = float(Y)
    return (fx >= fy)


def smallerOrEqual(X, Y):
    fx = float(X)
    fy = float(Y)
    return (fx <= fy)

def mergeTransaction(Tx1,Tx2,v1,v2):
    tx = Tx1+";"+Tx2
    fv1 = float(v1)
    fv2 = float(v2)
    fv = fv1
    if (fv > fv2):
        fv = fv2
    return tx,fv
def absdiffPercent(X, Ex, P):
    fx = float(X)
    fex = float(Ex)
    fp = float(P)
    if fex == 0 :
        fe = abs(fx-fex)
    else :
        fe = abs(fx-fex) * fp / abs(fex)
    #print(fe)
    return float("{0:.2f}".format(fe))

from datetime import datetime
def afterDate(D1,D2):
    date1 = datetime.strptime(str(D1), '%Y-%m-%d')
    date2 = datetime.strptime(str(D2), '%Y-%m-%d')
    if (date1 > date2) :
        return True
    return False

def year(CY,CP,X,D1,Y):
    date1 = datetime.strptime(str(D1), '%Y-%m-%d')
    if str(date1.year) == str(Y) :
        #print("checkFI",";",CY,";",CP,";",X,";")
        #print("checkFI",";",CY,";",CP,";",X,";")
        return True
    return False

sumlist = {}
validatedXBRL = {}

ratelist = {}

def calculateRate(V1, V2, P) :
    #print("checkRateSuitalbe:")
    if float(V2) == 0 :
        return 1000000000
    return str(float(V1) * float(P)/float(V2))

def calculateDiffRate(CY, CT1,CT2, V1,V2,Threshold) :
    V = abs(float(V1) - float(V2))
    V = float("{0:.2f}".format(V))
    if (V > float(Threshold)) :
        #print("calculateDiffRate",CY,";", CT1,";",CT2 ,";",False,";",V)
        return (False,V)
    else:
        #print("calculateDiffRate",CY,";", CT1,";",CT2 ,";",True,";",V,";")
        return (True,V)

#########################################################
def presumXBRL(CY, CP,CT, CL,GR, Ex, P, V):
    global sumlist
    S = str(CY+";"+CP + ";" + CT + ";" + CL + ";" + GR)
    if S not in sumlist:
        sumlist[S] = {}
        sumlist[S]["expectedResult"] = float(Ex)
        sumlist[S]["currentResult"] = 0
        sumlist[S]["threshold"] = 0

    fv = float(V)
    sumlist[S]["currentResult"] = sumlist[S]["currentResult"] + fv
    sumlist[S]["threshold"] = absdiffPercent(sumlist[S]["currentResult"], sumlist[S]["expectedResult"], P)

    return str(sumlist[S])

companyList = {}

def checkFact(CY, CP,CT,CL,GR) :
    global companyList
    global sumlist

    S = str(CY+";"+CP + ";" + CT + ";" + CL + ";" + GR)
    if CY not in companyList :
        companyList[CY] = {}
        companyList[CY]["maxThreshold"] = 0
        companyList[CY]["valid"] = False
    if S not in sumlist :
        return (False,0)
    #print("AA:",sumlist[S]["threshold"]," ",companyList[CY]["maxThreshold"])
    if sumlist[S]["threshold"] > companyList[CY]["maxThreshold"] :
        #print("BB:",sumlist[S]["threshold"])
        companyList[CY]["maxThreshold"] = sumlist[S]["threshold"]

    return (True, str(sumlist[S]["threshold"]))

def isValidatedXBRL(CY, thresholdLimit):
    thresholdLimit = float(thresholdLimit)
    #print("AA")
    if CY not in companyList :
        return (False,"")
    #print(">>",thresholdLimit," ",companyList[CY]["maxThreshold"])
    if thresholdLimit < float(companyList[CY]["maxThreshold"]) :
        return (False,str(companyList[CY]["maxThreshold"]))

    return (True, str(companyList[CY]["maxThreshold"]))

def presumTx(NX,NY,V):
    global sumlist
    s = NX+";"+NY
    if s not in sumlist :
        sumlist[s] = 0
    fv = float(V)
    sumlist[s] = sumlist[s] + fv
    return str(sumlist[s])
def latestsumTx(NX,NY):
    s = NX + ";" + NY
    if s not in sumlist :
        sumlist[s] = 0

    return str(sumlist[s])

digitCountAll = {}
def firstDigitAll(s):
    global digitCountAll
    rs = s[0]
    if rs not in digitCountAll :
        digitCountAll[rs] = 1
    else :
        digitCountAll[rs] = digitCountAll[rs] + 1
    return rs

def getdigitCountAll(s) :
    if s not in digitCountAll :
        return 1

    return digitCountAll[s]

percentNumAll = {}
sumdigitCountAll = -1

def percentNumberAll(s,BL):
    global percentNumAll
    global sumdigitCountAll

    if s == "-" or s == "0":
        return ("0","0")

    if sumdigitCountAll == -1 :
        sumdigitCountAll = 0
        for i in digitCountAll :
            if i != "-" and i!= "0" :
                #print(i," ",digitCountAll[i])
                sumdigitCountAll = sumdigitCountAll + digitCountAll[i]

    if s in digitCountAll :
            percentNumAll[s] = {}
            percentNumAll[s]["value"] = digitCountAll[s] * 100 / sumdigitCountAll
            percentNumAll[s]["diffBL"] = (float(BL) - percentNumAll[s]["value"])*(float(BL) - percentNumAll[s]["value"])
    
    if s not in percentNumAll :
        percentNumAll[s] = {}
        percentNumAll[s]["value"] = 0
        percentNumAll[s]["diffBL"] = float(BL) * float(BL)

    return (float("{0:.2f}".format(percentNumAll[s]["value"])), float("{0:.2f}".format(percentNumAll[s]["diffBL"])))


def isValidBenfordLawAll(TH):
    sumDiff = 0
    for i in percentNumAll :
        sumDiff  = sumDiff + percentNumAll[i]["diffBL"]
    #diff = float("{0:.2f}".format( abs(sumDiff - float(TH)) ))
    if sumDiff > float(TH) : 
        return ("False", float("{0:.2f}".format( sumDiff )))
    return ("True",float("{0:.2f}".format( sumDiff )))

################################
digitCountInCY = {}
def firstDigitInCY(CY, s):
    global digitCountInCY
    rs = s[0]
    if CY not in digitCountInCY :
        digitCountInCY[CY] = {
            "count" : {"1":0,"2":0,"3":0,"4":0,"5":0,"6":0,"7":0,"8":0,"9":0,"0":0,"-":0},
            "sumCount" : 0,
            "percent" : {"1":0,"2":0,"3":0,"4":0,"5":0,"6":0,"7":0,"8":0,"9":0},
            "diffPercent" : {"1":0,"2":0,"3":0,"4":0,"5":0,"6":0,"7":0,"8":0,"9":0},
            "diffBenfordLaw" : -1
        }
    digitCountInCY[CY]["count"][rs] = digitCountInCY[CY]["count"][rs] + 1
    if rs != "-" and rs != "0":
        digitCountInCY[CY]["sumCount"] = digitCountInCY[CY]["sumCount"] + 1

    return rs

def percentCountInCY(CY, s, BL) :
    if s != "-" and s != "0":
        global digitCountInCY
        digitCountInCY[CY]["percent"][s] = digitCountInCY[CY]["count"][s] * 100 / digitCountInCY[CY]["sumCount"]
        digitCountInCY[CY]["diffPercent"][s] = (float(BL) - digitCountInCY[CY]["percent"][s])*(float(BL) - digitCountInCY[CY]["percent"][s])
        return (
                digitCountInCY[CY]["count"][s], 
                float("{0:.2f}".format( digitCountInCY[CY]["percent"][s] )),
                float("{0:.2f}".format( digitCountInCY[CY]["diffPercent"][s] ))
                )
    return (digitCountInCY[CY]["count"][s],0,0)

def isValidBenfordLawInCY(CY,TH):
    if digitCountInCY[CY]["diffBenfordLaw"] == -1 :
        sumDiff = 0
        for i in digitCountInCY[CY]["diffPercent"] :
            sumDiff  = sumDiff + digitCountInCY[CY]["diffPercent"][i]
        #diff = float("{0:.2f}".format( abs(sumDiff - float(TH)) ))
        digitCountInCY[CY]["diffBenfordLaw"] = sumDiff

    sumDiff = digitCountInCY[CY]["diffBenfordLaw"]
    if sumDiff > float(TH) : 
        return ("False", float("{0:.2f}".format( sumDiff )))
    return ("True",float("{0:.2f}".format( sumDiff )))

#print(firstDigitAll("0.0"))
#http://tebeni.infocamere.it/teniWeb/jsp/index.jsp
#print(presumXBRL("C1","A","context","cl","gr","15","100","1000"));
#print(presumXBRL("C1","C","context","cl","gr","15","100","1000"));
#print(presumXBRL("C1","C","context","cl","gr","15","100","1000"));
#print(">>")
#print(isValidatedXBRL("C1",7000));
#print(sumlist)
#print(isValidatedXBRL("C1",500))
#print(isValidatedXBRL("C2",500))

#digitCountAll["0"] = 1
#digitCountAll["1"] = 1
#digitCountAll["2"] = 5
#digitCountAll["-"] = 2
#print(percentNumAllber("0"))
#print(percentNumAllber("-"))
#print(percentNumAllber("1"))
#print(percentNumAllber("2"))
