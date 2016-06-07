package se.relnah.q3.logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTools {

    /*
     * 
     * This class contains 'handy' functions used by the other Q3Log classes.
     * 
     * You are free to distribute this file as long as you don't charge anything
     * for it. If you want to put it on a magazine CD/DVD then e-mail me at
     * givememoney@wilf.co.uk and we can talk. You are also free to modify this
     * file as much as you want, but if you distribute a modified copy please
     * include this at the top of the file and be aware it remains the property
     * of me, so no selling modified version please.
     * 
     * Feel free to e-mail me with comments/suggestions/whatever at
     * q3logger@wilf.co.uk
     * 
     * Copyright Stuart Butcher (stu@wilf.co.uk) 2002
     * 
     */
    private Logger LOG = LoggerFactory.getLogger(LogTools.class);

    // The following Strings are globally used variables defined once here

    // The line seperator for this system
    public static final String LINESEP = System.getProperty("line.separator");

    // The directory seperator for this system
    public static final String DIRSEP = System.getProperty("file.separator");

    // The text used to specifiy that a weapon in the weapons list should be
    // linked with another one
    public static final String WEAPONLINK = new String("LINKWEAP");

    // Returns true if the passed file is valid, false if it isnt
    //
    // Written by : Stuart Butcher
    //
    // Date : 22nd September 2002
    //
    //
    public boolean checkFile(String sFile) throws IllegalArgumentException, FileNotFoundException {
        File fCheck;
        boolean bReturn = false;
        if ((sFile != null) && (!sFile.equals(""))) {
            fCheck = new File(sFile);
            if (fCheck.exists()) {
                bReturn = true;
            } else {
                throw new FileNotFoundException("File " + sFile + " not found.");
            }
        } else {
            throw new IllegalArgumentException("Input File Invalid");
        }
        return bReturn;
    }

    // Returns true if the passed string is not null or empty, otherwise returns
    // false
    //
    // Written by : Stuart Butcher
    //
    // Date : 22nd September 2002
    //
    //
    public boolean checkString(String sString) {
        boolean bReturn = false;
        if ((sString != null) && (!sString.equals(""))) {
            bReturn = true;
        }
        return bReturn;
    }

    // Returns the passed string with a "/" or "\" at the end if it doesnt have
    // one
    //
    // Written by : Stuart Butcher
    //
    // Date : 12th September 2002
    //
    //
    public String checkDir(String sDir) {
        String sEnd = new Character(sDir.charAt(sDir.length() - 1)).toString();
        String sReturn = new String(sDir);
        if (!sEnd.equals(DIRSEP)) {
            sReturn = sReturn + DIRSEP;
        }
        return sReturn;
    }

    // Returns the passed in string minus special characters (denoted by '^##')
    //
    // Written by : Stuart Butcher
    //
    // Date : 12th September 2002
    //
    //
    public String stripChars(String sUser, String[] aColours) {
        String sRep;
        String sMarker = "^#";
        String sEndTag = new String("</span>");
        String sBefore = new String("<span style='color: ");
        String sMiddle = new String(";'>" + sMarker);

        sUser = replaceAll(sUser, "<", "(");
        sUser = replaceAll(sUser, ">", ")");
        sUser = replaceAll(sUser, "*", "_");
        sUser = replaceAll(sUser, "?", "_");
        sUser = replaceAll(sUser, "|", "_");
        sUser = replaceAll(sUser, ":", "_");
        sUser = replaceAll(sUser, "\\", "_");
        sUser = replaceAll(sUser, "/", "_");
        sUser = replaceAll(sUser, "\"", "_");
        sUser = replaceAll(sUser, "^B", "");
        sUser = replaceAll(sUser, "^b", "");
        sUser = replaceAll(sUser, "^F", "");
        sUser = replaceAll(sUser, "^f", "");
        sUser = replaceAll(sUser, "^N", "");
        sUser = replaceAll(sUser, "^n", "");
        for (int iLoop = 0; iLoop < 8; iLoop++) {
            if ((aColours != null) && (aColours.length > iLoop)) {
                sRep = sBefore + aColours[iLoop] + sMiddle;
            } else {
                sRep = new String();
            }
            sUser = replaceAll(sUser, "^" + iLoop, sRep);
        }

        if (aColours.length > 0) {
            sUser = endHtmlTags(sUser, sMarker, sEndTag);
        }

        return sUser;
    }

    /**
     * Close open HTML tags using special marker string
     * 
     * @param sUser
     *            User nick name
     * @param sMarker
     *            Special string marking end of opening tag
     * @param sEndTag
     *            End tag
     * @return String with closed HTML tags
     */
    private String endHtmlTags(String sUser, String sMarker, String sEndTag) {

        // <span style='color: Red;'>^#[ADM]<span style='color: Blue;'>^#Omni
        while (sUser.contains(sMarker)) {
            int iOpenTagEnd = sUser.indexOf(sMarker);
            String sBegin = sUser.substring(0, iOpenTagEnd);

            // Skip marker
            String sAfterOpenTag = sUser.substring(iOpenTagEnd + sMarker.length());
            int iNextOpenTagStart = sAfterOpenTag.indexOf('<');

            // Last opening tag is closed at end of nick
            if (iNextOpenTagStart == -1) {
                iNextOpenTagStart = sAfterOpenTag.length();
            }

            String sTextInTag = sAfterOpenTag.substring(0, iNextOpenTagStart);
            String sAfterEndTag = sAfterOpenTag.substring(iNextOpenTagStart);

            sUser = sBegin + sTextInTag + sEndTag + sAfterEndTag;
        }
        return sUser;
    }

    public String stripChars(String sUser) {
        return stripChars(sUser, new String[0]);
    }

    // Makes sure the directory passed exists
    //
    // Written by : Stuart Butcher
    //
    // Date : 12th September 2002
    //
    //
    public boolean makeDirs(File fFile) {
        return fFile.mkdirs();
    }

    public boolean makeDirs(String sFile) {
        return makeDirs(new File(sFile));
    }

    // Makes sure that the passed in string is in the format '####.##' or
    // similar, and passes it back
    //
    // Written by : Stuart Butcher
    //
    // Date : 12th September 2002
    //
    //
    public String decimalPlaces(String sValue) {
        String sBefore = new String();
        String sAfter = new String();
        int iPos = sValue.indexOf(".");
        int iLen = sValue.length();
        if (iPos > -1) {
            if (iPos != 0) {
                if (iPos != iLen) {
                    sBefore = sValue.substring(0, iPos);
                    sAfter = sValue.substring(iPos + 1);
                } else {
                    sBefore = sValue.substring(0, iLen - 1);
                    sAfter = "00";
                }
            } else {
                sBefore = "0";
                sAfter = sValue.substring(1);
            }
        } else {
            sBefore = sValue;
            sAfter = "00";
        }
        if (sAfter.length() > 2) {
            sAfter = sAfter.substring(0, 2);
        }
        while (sAfter.length() < 2) {
            sAfter = sAfter + "0";
        }
        return sBefore + "." + sAfter;
    }

    // Returns a vector of the lines of the passed in file
    //
    // Written by : Stuart Butcher
    //
    // Date : 13th September 2002
    //
    //
    public Vector readFile(File fIn) throws IllegalArgumentException, FileNotFoundException, IOException {
        BufferedReader brIn;
        Vector vReturn = new Vector();
        String sLine = new String();
        if ((fIn.exists()) && (fIn.isFile())) {
            brIn = new BufferedReader(new FileReader(fIn));
            sLine = brIn.readLine();
            if (sLine != null) {
                while (sLine != null) {
                    vReturn.add(sLine);
                    sLine = brIn.readLine();
                }
            } else {
                LOG.info("File " + fIn.getPath() + " is empty");
            }
        } else {
            throw new FileNotFoundException("File " + fIn.getPath() + " doesn't exist");
        }
        return vReturn;
    }

    // Returns a string representation of the passed in vector
    //
    // Written by : Stuart Butcher
    //
    // Date : 13th September 2002
    //
    //
    public String getString(Vector vIn) {
        String sReturn = new String();
        String sLine = new String();
        Object objHold;
        for (Iterator itLocal = vIn.iterator(); itLocal.hasNext();) {
            sReturn = sReturn + LINESEP + (String) itLocal.next();
        }
        return sReturn;
    }

    // Replaces all occurances of sReplace in sMain with sWith (like
    // String.replaceAll but without regexp)
    // This is not just here to make this program work with Java 2 1.2.2 or
    // later. It is used when regexp
    // is an annoyance.
    //
    // Written by : Stuart Butcher
    //
    // Date : 13th September 2002
    //
    //
    public static String replaceAll(String sMain, String sReplace, String sWith) {
        int iLen = sReplace.length();
        int iStart;
        int iEnd;
        String sReturn = new String(sMain);
        if ((sMain.length() > sReplace.length()) && (sMain.indexOf(sReplace) > -1)) {
            sReturn = new String();
            iStart = sMain.indexOf(sReplace);
            while (iStart > -1) {
                iEnd = iStart + iLen - 1;
                if (iStart == 0) {
                    sReturn = sReturn + sWith;
                } else {
                    sReturn = sReturn + sMain.substring(0, iStart) + sWith;
                }
                sMain = sMain.substring(iEnd + 1);
                iStart = sMain.indexOf(sReplace);
            }
            sReturn = sReturn + sMain;
        } else if (sMain.equals(sReplace)) {
            sReturn = sWith;
        }
        return sReturn;
    }

    // Returns a vector of keys that link to the hashtable passed in but sorted
    //
    // Written by : Stuart Butcher
    //
    // Date : 22nd September 2002
    //
    //
    public Vector sortHash(Hashtable htIn, int iSort, boolean bForward) {
        Float[] fSort;
        Float[] fValue = new Float[htIn.size()];
        Hashtable htHold = new Hashtable();
        Vector vReturn = new Vector();
        Float fHold = new Float(0);
        String sHold = new String();
        String sOld = new String();
        String sValue = new String();
        String sUser = new String();
        String[] aUser;
        int iCount = 0;
        int iNumber = 0;
        for (Enumeration eSort = htIn.keys(); eSort.hasMoreElements();) {
            sUser = (String) eSort.nextElement();
            fSort = (Float[]) htIn.get(sUser);
            fHold = fSort[iSort];
            fValue[iCount] = fHold;
            sValue = fHold.toString();
            sHold = (String) htHold.get(sValue);
            if (htHold.get(sValue) != null) {
                sUser = sHold + "*" + sUser;
            }
            htHold.put(sValue, sUser);
            iCount++;
        }
        if (bForward) {
            Arrays.sort(fValue);
        } else {
            Arrays.sort(fValue, Collections.reverseOrder());
        }
        for (int iLoop = 0; iLoop < fValue.length; iLoop++) {
            fHold = fValue[iLoop];
            sUser = (String) htHold.get(fHold.toString());
            if (!sOld.equals(sUser)) {
                iNumber = 0;
            }
            sOld = sUser;
            if (sUser.indexOf("*") > -1) {
                aUser = split(sUser, "*");
                sUser = aUser[iNumber];
                iNumber++;
            } else {
                iNumber = 0;
            }
            fSort = (Float[]) htIn.get(sUser);
            vReturn.add(sUser);
        }
        return vReturn;
    }

    // Function to return an array from a string broken up by a splitting string
    // This is only here to make this program work with Java 2 1.2.2 and later
    // If you have Java 2 1.4 or later and want to use the String.split
    // function,
    // feel free
    //
    // Written by : Stuart Butcher
    //
    // Date : 4th August 2002
    //
    public static String[] split(String sCheck, String sChar) {
        String[] aReturn;
        ArrayList lReturn = new ArrayList();
        int iPos, iPos2;
        iPos = sCheck.indexOf(sChar);
        if (iPos > -1) {
            lReturn.add(sCheck.substring(0, iPos));
            while (iPos > -1) { // Loop through all occurances
                iPos2 = sCheck.indexOf(sChar, iPos + 1);
                if (iPos2 == -1) {
                    iPos2 = sCheck.length();
                }
                lReturn.add(sCheck.substring(iPos + 1, iPos2));
                iPos = sCheck.indexOf(sChar, iPos + 1);
            }
        } else {
            lReturn.add(sCheck);
        }
        aReturn = new String[lReturn.size()];
        for (int iLoop = 0; iLoop < lReturn.size(); iLoop++) {
            aReturn[iLoop] = (String) lReturn.get(iLoop);
        }
        return aReturn;
    }

}