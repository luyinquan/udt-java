/*********************************************************************************
 * Copyright (c) 2010 Forschungszentrum Juelich GmbH 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * (1) Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the disclaimer at the end. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * (2) Neither the name of Forschungszentrum Juelich GmbH nor the names of its 
 * contributors may be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/

package udt.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import udt.UDTPacket;
import udt.UDTSession;

public abstract class ControlPacket implements UDTPacket{
	
	protected int contrlPktTyp;

	//used for ACK and ACK2
	protected long ackSequenceNumber;
	
	protected long messageNumber;
	
	protected long timeStamp;
	
	protected long destinationID;
	
	protected byte[] controlInformation;
    
	private UDTSession session;
	
	public ControlPacket(){
    	
    }
    
	
	public int getControlPaketType() {
		return contrlPktTyp;
	}


	public void setControlPaketType(int packetTyp) {
		this.contrlPktTyp = packetTyp;
	}


	public long getAckSequenceNumber() {
		return ackSequenceNumber;
	}
	 public void setAckSequenceNumber(long ackSequenceNumber) {
		this.ackSequenceNumber = ackSequenceNumber;
	}


	public long getMessageNumber() {
		return messageNumber;
	}
	public void setMessageNumber(long messageNumber) {
		this.messageNumber = messageNumber;
	}
	
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	public long getDestinationID() {
		return destinationID;
	}
	public void setDestinationID(long destinationID) {
		this.destinationID = destinationID;
	}
	
	
	/**
	 * return the header according to specification p.5
	 * @return
	 */
	//TODO order?!?!?
	public byte[] getHeader(){
//		//sequence number with highest bit set to "0"
		try{
			ByteArrayOutputStream bos=new ByteArrayOutputStream(16);
			bos.write(PacketUtil.encodeHighesBitTypeAndSeqNumber(true, contrlPktTyp, ackSequenceNumber));
			bos.write(PacketUtil.encode(messageNumber));
			bos.write(PacketUtil.encode(timeStamp));
			bos.write(PacketUtil.encode(destinationID));
			return bos.toByteArray();
		}catch(IOException ioe){/*can't happen*/
			return null;
		}
	}
	
	/**
	 * this method builds the control information
	 * from the control parameters
	 * @return
	 */
	public abstract byte[] encodeControlInformation(); 

	/**
	 * complete header+ControlInformation packet for transmission
	 */
	
	public byte[] getEncoded(){
		byte[] header=getHeader();
		byte[] controlInfo=encodeControlInformation();
		byte[] result=controlInfo!=null?
				new byte[header.length + controlInfo.length]:
				new byte[header.length]; 
		System.arraycopy(header, 0, result, 0, header.length);
		if(controlInfo!=null){
			System.arraycopy(controlInfo, 0, result, header.length, controlInfo.length);
		}
		return result;
		
	};

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControlPacket other = (ControlPacket) obj;
		if (ackSequenceNumber != other.ackSequenceNumber)
			return false;
		if (contrlPktTyp != other.contrlPktTyp)
			return false;
		//if (!Arrays.equals(controlInformation, other.controlInformation))
		//	return false;
		if (destinationID != other.destinationID)
			return false;
		if (messageNumber != other.messageNumber)
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}

	public boolean isControlPacket(){
		return true;
	}
	
	public boolean forSender(){
		return true;
	}
	
	public boolean isConnectionHandshake(){
		return false;
	}
	
	public UDTSession getSession() {
		return session;
	}

	public void setSession(UDTSession session) {
		this.session = session;
	}
	
	public long getPacketSequenceNumber(){
		return -1;
	}
	
	public int compareTo(UDTPacket other){
		return (int)(getPacketSequenceNumber()-other.getPacketSequenceNumber());
	}
	
	public static enum ControlPacketType {
		
		CONNECTION_HANDSHAKE,
		KEEP_ALIVE,
		ACK,
		NAK,
		UNUNSED_1,
		SHUTDOWN,
		ACK2,
		MESSAGE_DROP_REQUEST,
		UNUNSED_2,
		UNUNSED_3,
		UNUNSED_4,
		UNUNSED_5,
		UNUNSED_6,
		UNUNSED_7,
		UNUNSED_8,
		USER_DEFINED,
		
	}
	
}
