package com.kanven.netty.codec.demux;

import java.util.List;

public interface DemuxDecoder {

	void decode(byte[] data, List<Object> out) throws Exception;

}
