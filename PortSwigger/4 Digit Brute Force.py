# Emulates a "Sniper attack" from Burp's Intruder tool.
import urllib
def queueRequests(target, wordlists):
    engine = RequestEngine(endpoint=target.endpoint,
                            concurrentConnections=5,
                            requestsPerConnection=100,
                            pipeline=False
                            )
    
    # Adjust the default_payloads list to match the number of payload positions
    # you have configured, and use strings for the default value of each.

    # Change the path to the wordlist or replace it with some other iterator.
    # Note the rest of the code assumes payloads are strings and need to be URL encoded.
    for x in range(0,10000):
        positions = "%04d" % (x,)
        engine.queue(target.req, positions)

def handleResponse(req, interesting):
    # currently available attributes are req.status, req.wordcount, req.length and req.response
    if req.status != 404:
        table.add(req)
