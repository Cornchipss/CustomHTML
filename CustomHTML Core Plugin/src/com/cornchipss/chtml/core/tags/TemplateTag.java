package com.cornchipss.chtml.core.tags;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cornchipss.chtml.CustomHTML;
import com.cornchipss.chtml.bexp.OutsidePattern;
import com.cornchipss.chtml.compiling.CHTMLCompiler;
import com.cornchipss.chtml.results.Result;
import com.cornchipss.chtml.tags.ICustomTag;
import com.cornchipss.chtml.util.DirectoryUtils;
import com.cornchipss.chtml.util.Helper;

public class TemplateTag implements ICustomTag
{
	@Override
	public Result[] use(String lines, Map<String, String> attributes, int tagStart, int tagEnd, Map<String, String> localVars, Map<String, String> outerVars)
	{
		String src = attributes.get("src");
		
		if(src != null)
		{
			src = DirectoryUtils.calculateRelativeDir(src);
			
			File templateFile = new File(src);
			
			Map<String, String> vars = new HashMap<>();
			String varsStr = attributes.get("vars");
			if(varsStr != null)
			{
				varsStr = Helper.removeTrailingWhiteSpace(varsStr);
				String[] varsSpaced = OutsidePattern.split(varsStr, " ", "\"");
				for(String s : varsSpaced)
				{
					String[] varsEqualed = OutsidePattern.split(s, "=", "\"");
					
					if(varsEqualed.length > 1)
					{
						vars.put(varsEqualed[0], varsEqualed[1].substring(1, varsEqualed[1].length() - 1));
					}
					else
					{
						vars.put(varsEqualed[0], "");
					}
				}
			}
			
			vars.putAll(localVars);
			
			String linesToCopy;
			try
			{
				linesToCopy = CHTMLCompiler.compile(templateFile, vars, false);
			}
			catch (IOException e)
			{
				CustomHTML.stopRunning("Template File (" + templateFile + ") Not Found!");
				return null;
			}
			
			if(linesToCopy.length() > 0)
			{
				linesToCopy = linesToCopy.substring(0, linesToCopy.length() - 1); // Remove the \n at the end
			}
			
			return new Result[] { new Result(linesToCopy, tagStart, tagEnd + 1) };
		}
		
		return null;
	}
	
	@Override
	public boolean hasPartner() { return false; }
	
	@Override
	public String getName() { return "template"; }
}
